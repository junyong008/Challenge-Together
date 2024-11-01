package com.yjy.feature.home

import com.yjy.common.core.util.TimeManager
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.home.HomeViewModel.Companion.SECONDS_PER_DAY
import com.yjy.feature.home.model.ChallengeSyncUiState
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.model.UnViewedNotificationUiState
import com.yjy.feature.home.model.UserNameUiState
import com.yjy.model.challenge.ChallengeFactory
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var timeManager: TimeManager
    private lateinit var viewModel: HomeViewModel

    private val timeChangedFlow = MutableSharedFlow<Unit>(replay = 1)
    private val tickerFlow = MutableStateFlow(LocalDateTime.now())
    private val startedChallengesFlow = MutableStateFlow(emptyList<StartedChallenge>())
    private val sortOrderFlow = MutableStateFlow(SortOrder.LATEST)
    private val currentTierFlow = MutableStateFlow(Tier.IRON)

    companion object {
        private const val SYNC_DELAY = 100L
        private const val SYNC_HALF_TIME = 50L
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        challengeRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        timeManager = mockk(relaxed = true)

        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Success(emptyList())
        coEvery { challengeRepository.startedChallenges } returns startedChallengesFlow
        coEvery { challengeRepository.sortOrder } returns sortOrderFlow
        coEvery { challengeRepository.currentTier } returns currentTierFlow
        coEvery { challengeRepository.setCurrentTier(any()) } just runs

        coEvery { userRepository.getUserName() } returns NetworkResult.Success("test")
        coEvery { userRepository.getUnViewedNotificationCount() } returns NetworkResult.Success(0)

        coEvery { timeManager.tickerFlow } returns tickerFlow
        coEvery { timeManager.timeChangedFlow } returns timeChangedFlow

        viewModel = HomeViewModel(
            timeManager = timeManager,
            userRepository = userRepository,
            challengeRepository = challengeRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestChallenge(
        id: String = "1",
        title: String = "Test Challenge",
        now: LocalDateTime,
        daysAgo: Long = 1,
        recordInDays: Long = 1,
    ) = ChallengeFactory.createStartedChallenge(
        id = id,
        title = title,
        description = "Description",
        category = Category.QUIT_DRUGS,
        targetDays = TargetDays.Fixed(days = 30),
        mode = Mode.CHALLENGE,
        recentResetDateTime = now.minusDays(daysAgo),
        currentRecordInSeconds = recordInDays * SECONDS_PER_DAY,
        isCompleted = false
    )

    private suspend fun TestScope.emitUpdatedState(
        sortOrder: SortOrder = SortOrder.LATEST,
        tier: Tier = Tier.IRON,
        challenges: List<StartedChallenge>,
        time: LocalDateTime
    ) {
        sortOrderFlow.emit(sortOrder)
        currentTierFlow.emit(tier)
        startedChallengesFlow.emit(challenges)
        tickerFlow.emit(time)
        advanceUntilIdle()
    }

    @Test
    fun `initial repository calls should set correct states`() = runTest {
        // Given
        var userName: UserNameUiState? = null
        var notification: UnViewedNotificationUiState? = null
        var syncState: ChallengeSyncUiState? = null
        var challenges: List<StartedChallenge>? = null
        var currentTier: Tier? = null

        // When: 초기 상태 수집
        val job = launch {
            launch { viewModel.userName.collect { userName = it } }
            launch { viewModel.unViewedNotificationState.collect { notification = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.startedChallenges.collect { challenges = it } }
            launch { viewModel.currentTier.collect { currentTier = it } }
        }
        advanceUntilIdle()

        // Then
        assertEquals(UserNameUiState.Success("test"), userName)
        assertEquals(UnViewedNotificationUiState.Success(0), notification)
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertTrue(challenges?.isEmpty() == true)
        assertEquals(Tier.IRON, currentTier)

        job.cancel()
    }

    @Test
    fun `failed challenge sync should set error state`() = runTest {
        // Given
        var syncState: ChallengeSyncUiState? = null
        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Failure.UnknownApiError(Throwable("Error"))

        // When: 동기화 실패 상태 수집
        val job = launch {
            launch { viewModel.challengeSyncState.collect { syncState = it } }
        }
        advanceUntilIdle()

        // Then
        assertEquals(ChallengeSyncUiState.Error, syncState)

        job.cancel()
    }

    @Test
    fun `time change should update started challenges`() = runTest {
        // Given
        var challenges: List<StartedChallenge>? = null
        var syncState: ChallengeSyncUiState? = null

        val now = LocalDateTime.now()
        val testChallenge = createTestChallenge(now = now)

        val job = launch {
            launch { viewModel.startedChallenges.collect { challenges = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.currentTier.collect {} }
        }

        // When: 초기 동기화 완료
        advanceUntilIdle()
        assertEquals(ChallengeSyncUiState.Success, syncState)

        // When: 챌린지 상태 업데이트
        emitUpdatedState(challenges = listOf(testChallenge), time = now)

        // Then
        assertEquals(listOf(testChallenge), challenges)

        job.cancel()
    }

    @Test
    fun `started challenges should not update during time sync but update after sync complete`() = runTest {
        // Given
        var challenges: List<StartedChallenge>? = null
        var syncState: ChallengeSyncUiState? = null
        val now = LocalDateTime.now()
        val initialChallenge = createTestChallenge(id = "1", title = "Initial Challenge", now = now)
        val newChallenge = initialChallenge.copy(id = "2", title = "New Challenge")

        coEvery { challengeRepository.syncChallenges() } coAnswers {
            delay(SYNC_DELAY)
            NetworkResult.Success(emptyList())
        }

        val job = launch {
            launch { viewModel.startedChallenges.collect { challenges = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.currentTier.collect {} }
        }

        // 초기 설정
        advanceUntilIdle()
        assertEquals(ChallengeSyncUiState.Success, syncState)

        emitUpdatedState(challenges = listOf(initialChallenge), time = now)
        assertEquals(listOf(initialChallenge), challenges)

        // When: 시간 변경으로 동기화 시작
        timeChangedFlow.emit(Unit)
        advanceTimeBy(SYNC_HALF_TIME)
        startedChallengesFlow.emit(listOf(newChallenge))
        tickerFlow.emit(now)

        // Then: 동기화 중에는 초기 상태 유지
        assertEquals(ChallengeSyncUiState.Loading.TimeSync, syncState)
        assertEquals(listOf(initialChallenge), challenges)

        // When: 동기화 완료
        advanceTimeBy(SYNC_HALF_TIME)
        advanceUntilIdle()

        // Then: 동기화 완료 후 새로운 상태로 업데이트
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertEquals(listOf(newChallenge), challenges)

        job.cancel()
    }

    @Test
    fun `challenges should be sorted by latest, oldest and title order`() = runTest {
        // Given
        var challenges: List<StartedChallenge>? = null
        var syncState: ChallengeSyncUiState? = null

        val now = LocalDateTime.now()
        val challenge1 = createTestChallenge(
            id = "1",
            title = "B Challenge",
            now = now,
            daysAgo = 5,
            recordInDays = 5
        )
        val challenge2 = createTestChallenge(
            id = "2",
            title = "A Challenge",
            now = now,
            daysAgo = 10,
            recordInDays = 10
        )

        val job = launch {
            launch { viewModel.startedChallenges.collect { challenges = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.currentTier.collect {} }
        }

        // When: 초기 동기화 완료
        advanceUntilIdle()
        assertEquals(ChallengeSyncUiState.Success, syncState)

        // When: 최신순으로 정렬
        emitUpdatedState(
            sortOrder = SortOrder.LATEST,
            challenges = listOf(challenge1, challenge2),
            time = now
        )

        // Then: ID 내림차순 정렬
        assertEquals(listOf(challenge2, challenge1), challenges)

        // When: 오래된순으로 정렬
        sortOrderFlow.emit(SortOrder.OLDEST)
        advanceUntilIdle()

        // Then: ID 오름차순 정렬
        assertEquals(listOf(challenge1, challenge2), challenges)

        // When: 제목순으로 정렬
        sortOrderFlow.emit(SortOrder.TITLE)
        advanceUntilIdle()

        // Then: 알파벳 순 정렬
        assertEquals(listOf(challenge2, challenge1), challenges)

        job.cancel()
    }

    @Test
    fun `completed challenge should trigger tier upgrade animation`() = runTest {
        // Given
        var uiState: HomeUiState? = null
        var syncState: ChallengeSyncUiState? = null
        var currentTier: Tier? = null

        val now = LocalDateTime.now()
        val challenge = createTestChallenge(
            now = now,
            daysAgo = 15,
            recordInDays = 15
        )

        val job = launch {
            launch { viewModel.uiState.collect { uiState = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.currentTier.collect { currentTier = it } }
            launch { viewModel.startedChallenges.collect { } }
        }

        // When: 초기 동기화 완료
        advanceUntilIdle()
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertEquals(Tier.IRON, currentTier)

        // When: 챌린지 완료로 티어 업그레이드 발생
        emitUpdatedState(challenges = listOf(challenge), time = now)

        // Then: 티어 업그레이드 애니메이션 상태 확인
        val animation = uiState?.tierUpAnimation
        assertNotNull(animation)
        assertEquals(Tier.IRON, animation.from)
        assertEquals(Tier.BRONZE, animation.to)

        job.cancel()
    }

    @Test
    fun `changing sort order should update repository and sort state`() = runTest {
        // Given
        var sortOrder: SortOrder? = null
        val newSortOrder = SortOrder.HIGHEST_RECORD

        val job = launch {
            viewModel.sortOrder.collect { sortOrder = it }
        }

        // When: 초기 상태 확인
        advanceUntilIdle()
        assertEquals(SortOrder.LATEST, sortOrder)

        // When: 정렬 순서 변경
        viewModel.processAction(HomeUiAction.OnSortOrderSelect(newSortOrder))
        sortOrderFlow.emit(newSortOrder)
        advanceUntilIdle()

        // Then
        assertEquals(newSortOrder, sortOrder)

        job.cancel()
    }

    @Test
    fun `retry action should restart challenge sync when in error state`() = runTest {
        // Given
        var syncState: ChallengeSyncUiState? = null
        var syncCallCount = 0

        coEvery { challengeRepository.syncChallenges() } answers {
            syncCallCount++
            if (syncCallCount == 1) {
                NetworkResult.Failure.UnknownApiError(Throwable("Error"))
            } else {
                NetworkResult.Success(emptyList())
            }
        }

        val job = launch {
            viewModel.challengeSyncState.collect { syncState = it }
        }

        // When: 최초 동기화 실패
        advanceUntilIdle()
        assertEquals(ChallengeSyncUiState.Error, syncState)

        // When: 재시도 액션 실행
        viewModel.processAction(HomeUiAction.OnRetryClick)
        advanceUntilIdle()

        // Then: 재시도 후 동기화 성공
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertEquals(2, syncCallCount)

        job.cancel()
    }
}
