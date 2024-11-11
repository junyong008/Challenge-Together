package com.yjy.feature.startedchallenge

import androidx.lifecycle.SavedStateHandle
import com.yjy.common.network.HttpStatusCodes.CONFLICT
import com.yjy.common.network.HttpStatusCodes.FORBIDDEN
import com.yjy.common.network.HttpStatusCodes.NOT_FOUND
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.domain.GetStartedChallengeDetailUseCase
import com.yjy.feature.startedchallenge.model.ChallengeDetailUiState
import com.yjy.feature.startedchallenge.model.StartedChallengeUiAction
import com.yjy.feature.startedchallenge.model.StartedChallengeUiEvent
import com.yjy.feature.startedchallenge.navigation.STARTED_CHALLENGE_ID
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StartedChallengeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: StartedChallengeViewModel
    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var getStartedChallengeDetail: GetStartedChallengeDetailUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val timeChangedFlow = MutableSharedFlow<Unit>()
    private val testChallengeId = "test-challenge-id"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.getStateFlow<String?>(STARTED_CHALLENGE_ID, null) } returns
            MutableStateFlow(testChallengeId)

        challengeRepository = mockk(relaxed = true)
        getStartedChallengeDetail = mockk()

        coEvery { challengeRepository.timeChangedFlow } returns timeChangedFlow

        viewModel = StartedChallengeViewModel(
            savedStateHandle = savedStateHandle,
            getStartedChallengeDetail = getStartedChallengeDetail,
            challengeRepository = challengeRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestChallengeDetail() = DetailedStartedChallenge(
        id = testChallengeId,
        title = "Test Challenge",
        description = "Test Description",
        category = Category.QUIT_DRUGS,
        targetDays = TargetDays.Fixed(days = 30),
        mode = Mode.CHALLENGE,
        startDateTime = LocalDateTime.now(),
        recentResetDateTime = LocalDateTime.now(),
        currentRecordInSeconds = 0,
        currentParticipantCounts = 1,
        isCompleted = false,
        rank = 1,
    )

    @Test
    fun `initial state should be loading`() = runTest {
        val job = launch { viewModel.challengeDetail.collect {} }
        assertEquals(ChallengeDetailUiState.Loading, viewModel.challengeDetail.value)
        job.cancel()
    }

    @Test
    fun `when subscription restarts, should fetch data without loading state`() = runTest {
        // Given
        val initialDetail = createTestChallengeDetail()
        val updatedDetail = initialDetail.copy(currentRecordInSeconds = 1000)
        val details = listOf(initialDetail, updatedDetail).iterator()

        coEvery { getStartedChallengeDetail(testChallengeId) } returns flow {
            emit(NetworkResult.Success(details.next()))
        }

        // When: 초기 구독
        var job = launch { viewModel.challengeDetail.collect {} }
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.challengeDetail.value is ChallengeDetailUiState.Success)
        assertEquals(
            initialDetail,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )

        // When: 구독이 끊기고 다시 시작 된다면
        job.cancel()
        job = launch { viewModel.challengeDetail.collect {} }
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.challengeDetail.value is ChallengeDetailUiState.Success)
        assertEquals(
            updatedDetail,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )
        job.cancel()
    }

    @Test
    fun `successful challenge detail fetch should update state`() = runTest {
        // Given
        val challengeDetail = createTestChallengeDetail()
        coEvery { getStartedChallengeDetail(testChallengeId) } returns flowOf(
            NetworkResult.Success(challengeDetail),
        )

        // When
        val job = launch { viewModel.challengeDetail.collect {} }
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.challengeDetail.value is ChallengeDetailUiState.Success)
        assertEquals(
            challengeDetail,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )
        job.cancel()
    }

    @Test
    fun `network error should emit network failure event and maintain loading state`() = runTest {
        // Given
        coEvery { getStartedChallengeDetail(testChallengeId) } returns flowOf(
            NetworkResult.Failure.NetworkError(Throwable()),
        )

        // When
        val job = launch { viewModel.challengeDetail.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(ChallengeDetailUiState.Loading, viewModel.challengeDetail.value)
        assertEquals(StartedChallengeUiEvent.LoadFailure.Network, viewModel.uiEvent.first())
        job.cancel()
    }

    @Test
    fun `http errors should emit corresponding failure events`() = runTest {
        // Given
        val testCases = listOf(
            NOT_FOUND to StartedChallengeUiEvent.LoadFailure.NotFound,
            CONFLICT to StartedChallengeUiEvent.LoadFailure.NotStarted,
            FORBIDDEN to StartedChallengeUiEvent.LoadFailure.NotParticipant,
        )

        testCases.forEach { (statusCode, expectedEvent) ->
            // When
            viewModel = StartedChallengeViewModel(
                savedStateHandle = savedStateHandle,
                getStartedChallengeDetail = getStartedChallengeDetail,
                challengeRepository = challengeRepository,
            )

            coEvery { getStartedChallengeDetail(testChallengeId) } returns flowOf(
                NetworkResult.Failure.HttpError(code = statusCode, body = "", message = null),
            )

            val job = launch { viewModel.challengeDetail.collect {} }
            val event = async { viewModel.uiEvent.first() }
            advanceUntilIdle()

            // Then
            assertEquals(ChallengeDetailUiState.Loading, viewModel.challengeDetail.value)
            assertEquals(expectedEvent, event.await())

            job.cancel()
        }
    }

    @Test
    fun `delete challenge success should emit success event`() = runTest {
        // Given
        coEvery { challengeRepository.deleteStartedChallenge(testChallengeId) } returns NetworkResult.Success(Unit)

        // When
        viewModel.processAction(StartedChallengeUiAction.OnDeleteChallengeClick(testChallengeId))
        advanceUntilIdle()

        // Then
        assertEquals(StartedChallengeUiEvent.DeleteSuccess, viewModel.uiEvent.first())
        assertFalse(viewModel.uiState.value.isDeleting)
    }

    @Test
    fun `delete challenge failure should emit failure event`() = runTest {
        // Given
        coEvery {
            challengeRepository.deleteStartedChallenge(testChallengeId)
        } returns NetworkResult.Failure.NetworkError(Throwable())

        // When
        viewModel.processAction(StartedChallengeUiAction.OnDeleteChallengeClick(testChallengeId))
        advanceUntilIdle()

        // Then
        assertEquals(StartedChallengeUiEvent.DeleteFailure, viewModel.uiEvent.first())
        assertFalse(viewModel.uiState.value.isDeleting)
    }

    @Test
    fun `reset challenge success should emit success event and restart detail flow`() = runTest {
        // Given
        val testMemo = "Test memo"
        val challengeDetail = createTestChallengeDetail()
        val changedChallenge = challengeDetail.copy(currentParticipantCounts = 1000)

        var detailCallCount = 0
        coEvery { getStartedChallengeDetail(testChallengeId) } answers {
            detailCallCount++
            when (detailCallCount) {
                1 -> flowOf(NetworkResult.Success(challengeDetail))
                else -> flowOf(NetworkResult.Success(changedChallenge))
            }
        }
        coEvery {
            challengeRepository.resetStartedChallenge(testChallengeId, testMemo)
        } returns NetworkResult.Success(Unit)

        // When: 초기 로드
        val job = launch { viewModel.challengeDetail.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(
            challengeDetail,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )

        // When: 챌린지 리셋 모의
        viewModel.processAction(StartedChallengeUiAction.OnResetClick(testChallengeId, testMemo))
        advanceUntilIdle()

        // Then
        assertEquals(StartedChallengeUiEvent.ResetSuccess, viewModel.uiEvent.first())
        assertEquals(
            changedChallenge,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )
        assertFalse(viewModel.uiState.value.isResetting)
        assertEquals(2, detailCallCount)

        job.cancel()
    }

    @Test
    fun `reset challenge failure should emit failure event`() = runTest {
        // Given
        val testMemo = "Test memo"
        val challengeDetail = createTestChallengeDetail()

        coEvery { getStartedChallengeDetail(testChallengeId) } returns flowOf(
            NetworkResult.Success(challengeDetail),
        )

        coEvery {
            challengeRepository.resetStartedChallenge(testChallengeId, testMemo)
        } returns NetworkResult.Failure.NetworkError(Throwable())

        // When
        val job = launch { viewModel.challengeDetail.collect {} }
        viewModel.processAction(StartedChallengeUiAction.OnResetClick(testChallengeId, testMemo))
        advanceUntilIdle()

        // Then
        assertEquals(StartedChallengeUiEvent.ResetFailure, viewModel.uiEvent.first())
        assertFalse(viewModel.uiState.value.isResetting)
        job.cancel()
    }

    @Test
    fun `time change should trigger detail refresh`() = runTest {
        // Given
        val initialDetail = createTestChallengeDetail()
        val updatedDetail = initialDetail.copy(currentRecordInSeconds = 1000)
        val details = listOf(initialDetail, updatedDetail).iterator()

        coEvery { getStartedChallengeDetail(testChallengeId) } returns flow {
            emit(NetworkResult.Success(details.next()))
        }

        // When: 초기 로드
        val job = launch { viewModel.challengeDetail.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(
            initialDetail,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )

        // When: 시간 변경 모의
        timeChangedFlow.emit(Unit)
        advanceUntilIdle()

        // Then
        assertEquals(
            updatedDetail,
            (viewModel.challengeDetail.value as ChallengeDetailUiState.Success).challenge,
        )
        job.cancel()
    }
}
