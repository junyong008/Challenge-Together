package com.yjy.feature.home

import com.yjy.common.core.util.TimeManager
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.home.HomeViewModel.Companion.SECONDS_PER_DAY
import com.yjy.model.challenge.ChallengeFactory
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var timeManager: TimeManager

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        challengeRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        timeManager = mockk(relaxed = true)

        // 기본적으로 초기 동기화 작업을 성공으로 모의하여 실패 케이스가 필요할 때만 각 테스트에서 다시 모의하도록 설정.
        coEvery { timeManager.setOnTimeChanged(any()) } returns Unit
        coEvery { timeManager.startTicking(any()) } returns Unit
        coEvery { timeManager.emitCurrentTime() } returns Unit
        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Success(emptyList())
        coEvery { userRepository.getUserName() } returns NetworkResult.Success("test")
        coEvery { userRepository.getUnViewedNotificationCount() } returns NetworkResult.Success(0)
        coEvery { challengeRepository.currentTier } returns flowOf(Tier.IRON)
        coEvery { challengeRepository.sortOrder } returns flowOf(SortOrder.OLDEST)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel init syncs data and sets no error state when all repository calls succeed`() = runTest {
        // Given
        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Success(emptyList())
        coEvery { userRepository.getUserName() } returns NetworkResult.Success("test")
        coEvery { userRepository.getUnViewedNotificationCount() } returns NetworkResult.Success(0)

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )
        advanceUntilIdle()

        // Then
        coVerify { challengeRepository.syncChallenges() }
        coVerify { userRepository.getUserName() }
        coVerify { userRepository.getUnViewedNotificationCount() }
        assertFalse(viewModel.uiState.value.hasError)
    }

    @Test
    fun `viewModel init sets error state when syncChallenges call fails`() = runTest {
        // Given
        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Failure.UnknownApiError(Throwable())
        coEvery { userRepository.getUserName() } returns NetworkResult.Success("test")
        coEvery { userRepository.getUnViewedNotificationCount() } returns NetworkResult.Success(0)

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.hasError)
    }

    @Test
    fun `viewModel init sets error state when getUserName call fails`() = runTest {
        // Given
        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Success(emptyList())
        coEvery { userRepository.getUserName() } returns NetworkResult.Failure.UnknownApiError(Throwable())
        coEvery { userRepository.getUnViewedNotificationCount() } returns NetworkResult.Success(0)

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.hasError)
    }

    @Test
    fun `handleTimeChange calls syncTime and pauses startedChallenges updates`() = runTest {
        // Given
        val now = LocalDateTime.now()
        val testChallenge = ChallengeFactory.createStartedChallenge(
            id = "1",
            title = "Test Challenge 1",
            description = "Description 1",
            category = Category.QUIT_DRUGS,
            targetDays = TargetDays.Fixed(days = 30),
            mode = Mode.CHALLENGE,
            recentResetDateTime = now.minusDays(1),
        )

        val syncTimeDelay = 500L
        val timeFlow = MutableSharedFlow<LocalDateTime>()
        val timeChangedCallback = slot<() -> Unit>()

        with(challengeRepository) {
            coEvery { startedChallenges } returns flowOf(listOf(testChallenge))
            coEvery { syncTime() } coAnswers {
                delay(syncTimeDelay)
                NetworkResult.Success(Unit)
            }
            coEvery { sortOrder } returns flowOf(SortOrder.LATEST)
        }

        with(timeManager) {
            coEvery { setOnTimeChanged(capture(timeChangedCallback)) } just runs
            coEvery { tickerFlow } returns timeFlow
            coEvery { emitCurrentTime() } coAnswers { timeFlow.emit(now) }
            coEvery { startTicking(any()) } just runs
        }

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )

        // 시간 동기화 중일 때는 챌린지 업데이트가 일시 중지됨
        timeChangedCallback.captured()
        advanceTimeBy(syncTimeDelay - 1)
        timeManager.emitCurrentTime()
        advanceUntilIdle()
        val startedChallengesWhenSyncing = viewModel.uiState.value.startedChallenges

        // 시간 동기화가 완료된 후에는 챌린지가 정상적으로 업데이트됨
        timeChangedCallback.captured()
        advanceTimeBy(syncTimeDelay + 1)
        timeManager.emitCurrentTime()
        advanceUntilIdle()
        val startedChallengesAfterSync = viewModel.uiState.value.startedChallenges

        // Then
        coVerify(exactly = 2) { challengeRepository.syncTime() }
        assertTrue(startedChallengesWhenSyncing.isEmpty())
        assertTrue(startedChallengesAfterSync.isNotEmpty())
    }

    @Test
    fun `startedChallenges updates are paused during loading and error states`() = runTest {
        // Given
        val testChallenge = ChallengeFactory.createStartedChallenge(
            id = "1",
            title = "Test Challenge 1",
            description = "Description 1",
            category = Category.QUIT_DRUGS,
            targetDays = TargetDays.Fixed(days = 30),
            mode = Mode.CHALLENGE,
            recentResetDateTime = LocalDateTime.now(),
        )

        with(challengeRepository) {
            coEvery { startedChallenges } returns flowOf(listOf(testChallenge))
            coEvery { syncChallenges() } returns NetworkResult.Failure.UnknownApiError(Throwable())
        }

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { challengeRepository.syncChallenges() }
        with(viewModel.uiState.value) {
            assertTrue(hasError)
            assertTrue(startedChallenges.isEmpty())
        }
    }

    @Test
    fun `emitCurrentTime triggers time calculation and tier update animation`() = runTest {
        // Given
        val now = LocalDateTime.now()
        val tier = Tier.IRON
        val testChallenge = ChallengeFactory.createStartedChallenge(
            id = "1",
            title = "Test Challenge 1",
            description = "Description 1",
            category = Category.QUIT_DRUGS,
            targetDays = TargetDays.Infinite,
            mode = Mode.CHALLENGE,
            recentResetDateTime = now.minusDays(30),  // 30일간 진행된 챌린지
        )

        val timeFlow = MutableSharedFlow<LocalDateTime>()

        with(challengeRepository) {
            coEvery { startedChallenges } returns flowOf(listOf(testChallenge))
            coEvery { currentTier } returns flowOf(tier)
            coEvery { setCurrentTier(any()) } returns Unit
        }

        with(timeManager) {
            coEvery { tickerFlow } returns timeFlow
            coEvery { startTicking(any()) } just runs
            coEvery { emitCurrentTime() } coAnswers { timeFlow.emit(now) }
        }

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )

        timeManager.emitCurrentTime()
        advanceUntilIdle()

        // Then
        val expectedRecordInSeconds = Duration.between(
            testChallenge.recentResetDateTime,
            now
        ).seconds.coerceAtMost((30 * SECONDS_PER_DAY).toLong())

        with(viewModel.uiState.value) {
            assertEquals(expectedRecordInSeconds, currentBestRecordInSeconds)
            assertEquals(listOf(Category.ALL, Category.QUIT_DRUGS), categories)
            assertEquals(1, startedChallenges.size)
            assertEquals(expectedRecordInSeconds, startedChallenges[0].currentRecordInSeconds)
            assertEquals(tier, tierUpAnimation?.from)
            assertEquals(Tier.getNextTier(tier), tierUpAnimation?.to)
        }
    }

    @Test
    fun `tier calculation includes completed challenges and triggers syncChallenges if necessary`() = runTest {
        // Given
        val now = LocalDateTime.now()
        val targetDays = 30
        val testChallenge = ChallengeFactory.createStartedChallenge(
            id = "1",
            title = "Test Challenge 1",
            description = "Description 1",
            category = Category.QUIT_DRUGS,
            targetDays = TargetDays.Fixed(days = targetDays),
            mode = Mode.CHALLENGE,
            recentResetDateTime = now.minusDays(targetDays.toLong() + 1), // targetDays보다 1일 더 지난 상태
            isCompleted = false  // 완료되지 않은 상태로 설정했지만 시간상으로는 이미 완료된 상태
        )

        val timeFlow = MutableSharedFlow<LocalDateTime>()

        with(challengeRepository) {
            coEvery { startedChallenges } returns flowOf(listOf(testChallenge))
            coEvery { currentTier } returns flowOf(Tier.IRON)
            coEvery { setCurrentTier(any()) } returns Unit
            coEvery { syncChallenges() } returns NetworkResult.Success(emptyList())
        }

        with(timeManager) {
            coEvery { tickerFlow } returns timeFlow
            coEvery { startTicking(any()) } just runs
            coEvery { emitCurrentTime() } coAnswers { timeFlow.emit(now) }
        }

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )

        timeManager.emitCurrentTime()
        advanceUntilIdle()

        // Then
        // 처음 ViewModel이 init 되고 한번, 시간이 emit 되고 나서 완료된 챌린지를 감지해 총 두번 이뤄져야 함
        coVerify(exactly = 2) { challengeRepository.syncChallenges() }
    }

    @Test
    fun `startedChallenges are sorted correctly based on sortOrder`() = runTest {
        // Given
        val now = LocalDateTime.now()
        val testChallenges = listOf(
            ChallengeFactory.createStartedChallenge(
                id = "1",
                title = "B Challenge",
                description = "Description 1",
                category = Category.QUIT_DRUGS,
                targetDays = TargetDays.Fixed(days = 30),
                mode = Mode.CHALLENGE,
                recentResetDateTime = now.minusDays(5),
            ),
            ChallengeFactory.createStartedChallenge(
                id = "2",
                title = "A Challenge",
                description = "Description 2",
                category = Category.QUIT_DRINKING,
                targetDays = TargetDays.Fixed(days = 30),
                mode = Mode.CHALLENGE,
                recentResetDateTime = now.minusDays(10),
            )
        )

        val sortOrderFlow = MutableStateFlow(SortOrder.LATEST)
        val timeFlow = MutableSharedFlow<LocalDateTime>()

        with(challengeRepository) {
            coEvery { startedChallenges } returns flowOf(testChallenges)
            coEvery { sortOrder } returns sortOrderFlow
        }

        with(timeManager) {
            coEvery { tickerFlow } returns timeFlow
            coEvery { startTicking(any()) } just runs
            coEvery { emitCurrentTime() } coAnswers { timeFlow.emit(now) }
        }

        // When
        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )

        timeManager.emitCurrentTime()
        advanceUntilIdle()

        // Then
        // 최신순 정렬 (id 기준 내림차순)
        assertEquals(
            listOf("2", "1"),
            viewModel.uiState.value.startedChallenges.map { it.id }
        )

        // 오래된순 정렬 (id 기준 오름차순)
        sortOrderFlow.value = SortOrder.OLDEST
        advanceUntilIdle()
        assertEquals(
            listOf("1", "2"),
            viewModel.uiState.value.startedChallenges.map { it.id }
        )

        // 제목순 정렬
        sortOrderFlow.value = SortOrder.TITLE
        advanceUntilIdle()
        assertEquals(
            listOf("A Challenge", "B Challenge"),
            viewModel.uiState.value.startedChallenges.map { it.title }
        )

        // 최고 기록순 정렬
        sortOrderFlow.value = SortOrder.HIGHEST_RECORD
        advanceUntilIdle()
        assertEquals(
            listOf(10L, 5L),
            viewModel.uiState.value.startedChallenges.map {
                it.currentRecordInSeconds!! / SECONDS_PER_DAY
            }
        )

        // 최저 기록순 정렬
        sortOrderFlow.value = SortOrder.LOWEST_RECORD
        advanceUntilIdle()
        assertEquals(
            listOf(5L, 10L),
            viewModel.uiState.value.startedChallenges.map {
                it.currentRecordInSeconds!! / SECONDS_PER_DAY
            }
        )
    }
}
