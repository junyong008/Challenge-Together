package com.yjy.feature.addchallenge

import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class AddChallengeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var startedChallengeRepository: StartedChallengeRepository
    private lateinit var viewModel: AddChallengeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        challengeRepository = mockk(relaxed = true)
        startedChallengeRepository = mockk(relaxed = true)

        viewModel = AddChallengeViewModel(challengeRepository, startedChallengeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processAction should update mode and send ModeSelected event`() = runTest {
        // Given
        val mode = Mode.FREE

        // When
        viewModel.processAction(AddChallengeUiAction.OnSelectMode(mode))

        // Then
        val state = viewModel.uiState.first()
        val event = viewModel.uiEvent.first()
        assertEquals(expected = mode, actual = state.mode)
        assertEquals(expected = AddChallengeUiEvent.ModeSelected, actual = event)
    }

    @Test
    fun `startChallenge with free mode should fix target day to infinite`() = runTest {
        // Given
        val mode = Mode.FREE
        val category = Category.QUIT_SMOKING
        val title = "Quit Smoking"
        val description = "A challenge to quit smoking"
        val targetDays = TargetDays.Fixed(30)
        val startDateTime = LocalDateTime.now()
        val challengeId = 777

        coEvery {
            challengeRepository.addChallenge(
                category = category,
                title = title,
                description = description,
                targetDays = TargetDays.Infinite,
                startDateTime = startDateTime,
                languageCode = "ko",
            )
        } returns NetworkResult.Success(challengeId)

        // When
        viewModel.processAction(
            AddChallengeUiAction.OnStartChallenge(
                mode = mode,
                category = category,
                title = title,
                description = description,
                startDateTime = startDateTime,
                targetDays = targetDays,
                languageCode = "ko",
            ),
        )
        advanceUntilIdle()

        // Then
        coVerify {
            challengeRepository.addChallenge(
                category = category,
                title = title,
                description = description,
                targetDays = TargetDays.Infinite,
                startDateTime = startDateTime,
                languageCode = "ko",
            )
        }
        val event = viewModel.uiEvent.first()
        assertEquals(expected = AddChallengeUiEvent.ChallengeStarted(challengeId), actual = event)
    }

    @Test
    fun `startChallenge should call addChallenge in repository and send ChallengeStarted event`() = runTest {
        // Given
        val mode = Mode.CHALLENGE
        val category = Category.QUIT_SMOKING
        val title = "Quit Smoking"
        val description = "A challenge to quit smoking"
        val targetDays = TargetDays.Fixed(30)
        val startDateTime = LocalDateTime.now()
        val challengeId = 777

        coEvery {
            challengeRepository.addChallenge(
                category = category,
                title = title,
                description = description,
                targetDays = targetDays,
                startDateTime = null,
                languageCode = "ko",
            )
        } returns NetworkResult.Success(challengeId)

        // When
        viewModel.processAction(
            AddChallengeUiAction.OnStartChallenge(
                mode = mode,
                category = category,
                title = title,
                description = description,
                startDateTime = startDateTime,
                targetDays = targetDays,
                languageCode = "ko",
            ),
        )
        advanceUntilIdle()

        // Then
        coVerify {
            challengeRepository.addChallenge(
                category = category,
                title = title,
                description = description,
                targetDays = targetDays,
                startDateTime = null,
                languageCode = "ko",
            )
        }
        val event = viewModel.uiEvent.first()
        assertEquals(expected = AddChallengeUiEvent.ChallengeStarted(challengeId), actual = event)
    }

    @Test
    fun `createWaitingRoom should call addChallenge in repository and send WaitingChallengeCreated event`() = runTest {
        // Given
        val category = Category.QUIT_SMOKING
        val title = "Quit Smoking"
        val description = "A challenge to quit smoking"
        val targetDays = TargetDays.Fixed(30)
        val maxParticipants = 5
        val roomPassword = "password123"
        val challengeId = 123

        coEvery {
            challengeRepository.addChallenge(
                category = category,
                title = title,
                description = description,
                targetDays = targetDays,
                maxParticipants = maxParticipants,
                roomPassword = roomPassword,
                languageCode = "ko",
            )
        } returns NetworkResult.Success(challengeId)

        // When
        viewModel.processAction(
            AddChallengeUiAction.OnCreateWaitingRoom(
                category = category,
                title = title,
                description = description,
                targetDays = targetDays,
                maxParticipants = maxParticipants,
                enableRoomPassword = true,
                roomPassword = roomPassword,
                languageCode = "ko",
            ),
        )
        advanceUntilIdle()

        // Then
        coVerify {
            challengeRepository.addChallenge(
                category = category,
                title = title,
                description = description,
                targetDays = targetDays,
                maxParticipants = maxParticipants,
                roomPassword = roomPassword,
                languageCode = "ko",
            )
        }
        val event = viewModel.uiEvent.first()
        assertEquals(expected = AddChallengeUiEvent.WaitingChallengeCreated(challengeId), actual = event)
    }

    @Test
    fun `updateStartDateTime should set valid date and time`() = runTest {
        // Given
        val selectedDate = LocalDate.of(1900, 1, 1)
        val selectedHour = 10
        val selectedMinute = 30
        val isAm = true
        val expectedDateTime = selectedDate.atTime(10, 30)

        // When
        viewModel.processAction(
            AddChallengeUiAction.OnStartDateTimeUpdated(
                selectedDate = selectedDate,
                hour = selectedHour,
                minute = selectedMinute,
                isAm = isAm,
            ),
        )

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = expectedDateTime, actual = state.startDateTime)
    }

    @Test
    fun `updateStartDateTime should trigger StartDateTimeOutOfRange event if date is in the past`() = runTest {
        // Given
        val selectedDate = LocalDate.of(3000, 1, 1)
        val selectedHour = 10
        val selectedMinute = 30
        val isAm = true

        // When
        viewModel.processAction(
            AddChallengeUiAction.OnStartDateTimeUpdated(
                selectedDate = selectedDate,
                hour = selectedHour,
                minute = selectedMinute,
                isAm = isAm,
            ),
        )

        // Then
        val state = viewModel.uiState.first()
        val event = viewModel.uiEvent.first()
        assertEquals(LocalDateTime.now().withNano(0), state.startDateTime.withNano(0))
        assertEquals(AddChallengeUiEvent.StartDateTimeOutOfRange, event)
    }
}
