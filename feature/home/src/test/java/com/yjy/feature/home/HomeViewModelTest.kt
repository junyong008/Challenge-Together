package com.yjy.feature.home

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.api.WaitingChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.GetStartedChallengesUseCase
import com.yjy.feature.home.model.ChallengeSyncUiState
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.model.TierUiState
import com.yjy.feature.home.model.TimeSyncUiState
import com.yjy.feature.home.model.UnViewedNotificationUiState
import com.yjy.feature.home.model.UserNameUiState
import com.yjy.feature.home.model.getTierOrDefault
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getStartedChallengesUseCase: GetStartedChallengesUseCase
    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var waitingChallengeRepository: WaitingChallengeRepository
    private lateinit var challengePreferencesRepository: ChallengePreferencesRepository
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: HomeViewModel

    private val startedChallengesFlow = MutableStateFlow(emptyList<SimpleStartedChallenge>())
    private val sortOrderFlow = MutableStateFlow(SortOrder.LATEST)
    private val localTierFlow = MutableStateFlow(Tier.IRON)
    private val timeChangedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val waitingChallengesFlow = MutableStateFlow(emptyList<SimpleWaitingChallenge>())
    private val recentCompletedFlow = MutableStateFlow(emptyList<String>())

    companion object {
        private const val SYNC_DELAY = 100L
        private const val SYNC_HALF_TIME = 50L
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        waitingChallengeRepository = mockk(relaxed = true)
        challengePreferencesRepository = mockk(relaxed = true)
        challengeRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        getStartedChallengesUseCase = mockk(relaxed = true)

        coEvery { challengeRepository.syncChallenges() } returns NetworkResult.Success(emptyList())
        coEvery { challengePreferencesRepository.timeChangedFlow } returns timeChangedFlow
        coEvery { waitingChallengeRepository.waitingChallenges } returns waitingChallengesFlow
        coEvery { challengePreferencesRepository.recentCompletedChallengeTitles } returns recentCompletedFlow
        coEvery { challengePreferencesRepository.sortOrder } returns sortOrderFlow
        coEvery { challengePreferencesRepository.localTier } returns localTierFlow
        coEvery { challengePreferencesRepository.setLocalTier(any()) } just runs
        coEvery { challengePreferencesRepository.getRemoteTier() } returns NetworkResult.Success(Tier.IRON)

        coEvery { userRepository.syncTime() } returns NetworkResult.Success(Unit)
        coEvery { userRepository.getUserName() } returns NetworkResult.Success("test")
        coEvery { userRepository.getUnViewedNotificationCount() } returns NetworkResult.Success(0)

        coEvery { getStartedChallengesUseCase() } returns startedChallengesFlow

        viewModel = HomeViewModel(
            getStartedChallengesUseCase = getStartedChallengesUseCase,
            userRepository = userRepository,
            waitingChallengeRepository = waitingChallengeRepository,
            challengeRepository = challengeRepository,
            challengePreferencesRepository = challengePreferencesRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestChallenge(
        id: Int = 1,
        title: String = "Test Challenge",
        recordInDays: Long = 1,
        isCompleted: Boolean = false,
    ) = SimpleStartedChallenge(
        id = id,
        title = title,
        description = "Description",
        category = Category.QUIT_DRUGS,
        targetDays = TargetDays.Fixed(days = 30),
        mode = Mode.CHALLENGE,
        recentResetDateTime = LocalDateTime.now(),
        currentRecordInSeconds = recordInDays * SECONDS_PER_DAY,
        isCompleted = isCompleted,
    )

    private suspend fun TestScope.emitUpdatedState(
        sortOrder: SortOrder = SortOrder.LATEST,
        tier: Tier = Tier.IRON,
        challenges: List<SimpleStartedChallenge>,
    ) {
        sortOrderFlow.emit(sortOrder)
        localTierFlow.emit(tier)
        startedChallengesFlow.emit(challenges)
        advanceUntilIdle()
    }

    @Test
    fun `initial repository calls should set correct states`() = runTest {
        // Given
        var userName: UserNameUiState? = null
        var notification: UnViewedNotificationUiState? = null
        var syncState: ChallengeSyncUiState? = null
        var timeSyncState: TimeSyncUiState? = null
        var challenges: List<SimpleStartedChallenge>? = null
        var tierState: TierUiState? = null

        // When: 초기 상태 수집
        val job = launch {
            launch { viewModel.userName.collect { userName = it } }
            launch { viewModel.unViewedNotificationState.collect { notification = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.timeSyncState.collect { timeSyncState = it } }
            launch { viewModel.startedChallenges.collect { challenges = it } }
            launch { viewModel.tierState.collect { tierState = it } }
        }
        advanceUntilIdle()

        // Then: 올바른 초기 상태가 설정되어야 한다
        assertEquals(UserNameUiState.Success("test"), userName)
        assertEquals(UnViewedNotificationUiState.Success(0), notification)
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertEquals(TimeSyncUiState.Success, timeSyncState)
        assertTrue(challenges?.isEmpty() == true)
        assertEquals(TierUiState.Success(Tier.IRON), tierState)

        job.cancel()
    }

    @Test
    fun `failed challenge sync should set error state`() = runTest {
        // Given
        var syncState: ChallengeSyncUiState? = null
        coEvery {
            challengeRepository.syncChallenges()
        } returns NetworkResult.Failure.UnknownApiError(Throwable("Error"))

        // When: 동기화 실패 상태 수집
        val job = launch {
            launch { viewModel.challengeSyncState.collect { syncState = it } }
        }
        advanceUntilIdle()

        // Then: 에러 상태여야 한다
        assertEquals(ChallengeSyncUiState.Error, syncState)

        job.cancel()
    }

    @Test
    fun `started challenges should not update during sync but update after sync completes`() = runTest {
        // Given
        var challenges: List<SimpleStartedChallenge>? = null
        var syncState: ChallengeSyncUiState? = null
        val initialChallenge = createTestChallenge(id = 1, title = "Initial Challenge")
        val newChallenge = initialChallenge.copy(id = 2, title = "New Challenge")

        coEvery { challengeRepository.syncChallenges() } coAnswers {
            delay(SYNC_DELAY)
            NetworkResult.Success(emptyList())
        }

        val job = launch {
            launch { viewModel.startedChallenges.collect { challenges = it } }
            launch { viewModel.challengeSyncState.collect { syncState = it } }
            launch { viewModel.tierState.collect {} }
        }

        // 초기 설정
        advanceUntilIdle()
        assertEquals(ChallengeSyncUiState.Success, syncState)

        emitUpdatedState(challenges = listOf(initialChallenge))
        assertEquals(listOf(initialChallenge), challenges)

        // When: challengeSyncState를 재시작하여 동기화 트리거
        viewModel.challengeSyncState.restart()
        advanceTimeBy(SYNC_HALF_TIME)
        startedChallengesFlow.emit(listOf(newChallenge))

        // Then: 동기화 중에는 챌린지가 업데이트되지 않아야 한다
        assertEquals(ChallengeSyncUiState.Loading, syncState)
        assertEquals(listOf(initialChallenge), challenges)

        // When: 동기화 완료
        advanceTimeBy(SYNC_HALF_TIME)
        advanceUntilIdle()

        // Then: 동기화 완료 후 챌린지가 업데이트되어야 한다
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertEquals(listOf(newChallenge), challenges)

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

        // Then: 정렬 순서가 업데이트되어야 한다
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

        // Then: 동기화가 재시작되어 성공해야 한다
        assertEquals(ChallengeSyncUiState.Success, syncState)
        assertEquals(2, syncCallCount)

        job.cancel()
    }

    @Test
    fun `completing a challenge should trigger challenge sync`() = runTest {
        // Given
        var syncCallCount = 0
        coEvery { challengeRepository.syncChallenges() } answers {
            syncCallCount++
            NetworkResult.Success(emptyList())
        }

        val initialChallenge = createTestChallenge(
            id = 1,
            recordInDays = 29, // 목표 일수 바로 아래
            isCompleted = false,
        )

        val job = launch {
            launch { viewModel.startedChallenges.collect { } }
            launch { viewModel.challengeSyncState.collect { } }
            launch { viewModel.tierState.collect { } }
        }

        // 초기 상태: 챌린지가 완료되지 않음
        startedChallengesFlow.emit(listOf(initialChallenge))
        advanceUntilIdle()
        assertEquals(1, syncCallCount) // 초기 동기화 호출

        // When: 챌린지가 완료됨 (currentRecordInSeconds가 targetDays를 초과)
        val completedChallenge = initialChallenge.copy(
            currentRecordInSeconds = 30 * SECONDS_PER_DAY, // 목표 일수 달성
        )
        startedChallengesFlow.emit(listOf(completedChallenge))
        advanceUntilIdle()

        // Then: 챌린지 동기화가 트리거되어야 한다
        assertEquals(2, syncCallCount) // 챌린지 완료로 인해 동기화 재호출

        job.cancel()
    }

    @Test
    fun `calculated tier higher than remote tier should request remote tier again`() = runTest {
        // Given
        var remoteTierCallCount = 0
        coEvery { challengePreferencesRepository.getRemoteTier() } answers {
            remoteTierCallCount++
            println(remoteTierCallCount)
            NetworkResult.Success(Tier.BRONZE)
        }
        localTierFlow.emit(Tier.BRONZE)

        var tierState: TierUiState? = null

        val job = launch {
            launch { viewModel.startedChallenges.collect { } }
            launch { viewModel.challengeSyncState.collect { } }
            launch { viewModel.tierState.collect { tierState = it } }
        }
        advanceUntilIdle()

        // When: 현재 챌린지들로 계산된 티어가 리모트 티어보다 높을 때
        val highTierChallenge = createTestChallenge(
            id = 1,
            recordInDays = Tier.SILVER.requireSeconds / SECONDS_PER_DAY, // SILVER 티어에 해당하는 일수
            isCompleted = false,
        )
        startedChallengesFlow.emit(listOf(highTierChallenge))
        advanceUntilIdle()

        // Then: 리모트 티어를 다시 요청해야 한다
        assertEquals(2, remoteTierCallCount) // 초기 호출 + 재요청
        assertEquals(TierUiState.Success(Tier.BRONZE), tierState)

        job.cancel()
    }

    @Test
    fun `local tier lower than remote tier should set animation state to next tier`() = runTest {
        // Given
        var uiState: HomeUiState? = null
        var tierState: TierUiState? = null

        coEvery { challengePreferencesRepository.getRemoteTier() } returns NetworkResult.Success(Tier.DIAMOND)
        localTierFlow.emit(Tier.BRONZE)

        val job = launch {
            launch { viewModel.uiState.collect { uiState = it } }
            launch { viewModel.tierState.collect { tierState = it } }
            launch { viewModel.startedChallenges.collect { } }
        }

        // When: 티어 상태를 가져올 때 로컬 티어가 리모트 티어보다 낮음
        viewModel.tierState.restart()
        advanceUntilIdle()

        // Then: 애니메이션 상태값이 설정되어야 한다
        assertEquals(TierUiState.Success(Tier.BRONZE), tierState)
        val animation = uiState?.tierUpAnimation
        assertNotNull(animation)
        assertEquals(Tier.BRONZE, animation.from)
        assertEquals(Tier.SILVER, animation.to)

        job.cancel()
    }

    @Test
    fun `dismissing tier animation with remote tier ahead should set next tier animation`() = runTest {
        // Given
        var uiState: HomeUiState? = null
        var tierState: TierUiState? = null
        val localTierSetCalls = mutableListOf<Tier>()

        coEvery { challengePreferencesRepository.setLocalTier(any()) } coAnswers {
            localTierSetCalls.add(firstArg())
            localTierFlow.emit(firstArg())
        }

        coEvery { challengePreferencesRepository.getRemoteTier() } returns NetworkResult.Success(Tier.GOLD)
        localTierFlow.emit(Tier.BRONZE)

        val job = launch {
            launch { viewModel.uiState.collect { uiState = it } }
            launch { viewModel.tierState.collect { tierState = it } }
            launch { viewModel.startedChallenges.collect { } }
        }

        // When: 티어 상태를 가져와 애니메이션이 설정됨
        viewModel.tierState.restart()
        advanceUntilIdle()

        // 첫 번째 애니메이션 확인
        var animation = uiState?.tierUpAnimation
        assertNotNull(animation)
        assertEquals(Tier.BRONZE, animation.from)
        assertEquals(Tier.SILVER, animation.to)
        assertEquals(Tier.BRONZE, tierState?.getTierOrDefault())

        // When: 첫 번째 애니메이션 dismiss 처리
        viewModel.processAction(HomeUiAction.OnDismissTierUpAnimation)
        advanceUntilIdle()

        // Then: localTier가 업데이트되고 다음 애니메이션이 설정되어야 함
        assertEquals(listOf(Tier.SILVER), localTierSetCalls)
        animation = uiState?.tierUpAnimation
        assertNotNull(animation)
        assertEquals(Tier.SILVER, animation.from)
        assertEquals(Tier.GOLD, animation.to)
        assertEquals(Tier.SILVER, tierState?.getTierOrDefault())

        // When: 두 번째 애니메이션 dismiss 처리
        viewModel.processAction(HomeUiAction.OnDismissTierUpAnimation)
        advanceUntilIdle()

        // Then: localTier가 업데이트되고 애니메이션이 종료되어야 함
        assertEquals(listOf(Tier.SILVER, Tier.GOLD), localTierSetCalls)
        assertEquals(Tier.GOLD, tierState?.getTierOrDefault())
        animation = uiState?.tierUpAnimation
        assertNull(animation)

        job.cancel()
    }
}
