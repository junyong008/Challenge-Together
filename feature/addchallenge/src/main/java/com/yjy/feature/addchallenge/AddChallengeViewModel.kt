package com.yjy.feature.addchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_DESCRIPTION_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TITLE_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_ROOM_PASSWORD_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.TimeConst.HOURS_PER_HALF_DAY
import com.yjy.common.core.constants.TimeConst.MIDNIGHT_HOUR
import com.yjy.common.core.constants.TimeConst.NOON_HOUR
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val startedChallengeRepository: StartedChallengeRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddChallengeUiState> = MutableStateFlow(AddChallengeUiState())
    val uiState: StateFlow<AddChallengeUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<AddChallengeUiEvent>()
    val uiEvent: Flow<AddChallengeUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: AddChallengeUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    suspend fun shouldShowAd(): Boolean {
        return startedChallengeRepository.startedChallenges
            .map { it.size >= MIN_CHALLENGES_FOR_AD }
            .first()
    }

    fun processAction(action: AddChallengeUiAction) {
        when (action) {
            is AddChallengeUiAction.OnSelectMode -> updateMode(action.mode)
            is AddChallengeUiAction.OnSelectCategory -> updateCategory(action.category, action.title)
            is AddChallengeUiAction.OnTitleUpdated -> updateTitle(action.title)
            is AddChallengeUiAction.OnDescriptionUpdated -> updateDescription(action.description)
            is AddChallengeUiAction.OnTargetDaysUpdated -> updateTargetDays(action.targetDays)
            is AddChallengeUiAction.OnMaxParticipantsUpdated -> updateMaxParticipants(action.maxParticipants)
            is AddChallengeUiAction.OnEnableRoomPasswordUpdated -> updateEnableRoomPassword(action.enableRoomPassword)
            is AddChallengeUiAction.OnRoomPasswordUpdated -> updateRoomPassword(action.roomPassword)

            is AddChallengeUiAction.OnStartDateTimeUpdated -> updateStartDateTime(
                selectedDate = action.selectedDate,
                selectedHour = action.hour,
                selectedMinute = action.minute,
                isAm = action.isAm,
            )

            is AddChallengeUiAction.OnStartChallenge -> startChallenge(
                mode = action.mode,
                category = action.category,
                title = action.title,
                description = action.description,
                startDateTime = action.startDateTime,
                targetDays = action.targetDays,
                languageCode = action.languageCode,
            )

            is AddChallengeUiAction.OnCreateWaitingRoom -> createWaitingRoom(
                category = action.category,
                title = action.title,
                description = action.description,
                targetDays = action.targetDays,
                maxParticipants = action.maxParticipants,
                enableRoomPassword = action.enableRoomPassword,
                roomPassword = action.roomPassword,
                languageCode = action.languageCode,
            )
        }
    }

    private fun updateMode(mode: Mode) {
        _uiState.update { it.copy(mode = mode) }
        sendEvent(AddChallengeUiEvent.ModeSelected)
    }

    private fun updateCategory(category: Category, title: String) {
        _uiState.update { it.copy(category = category, title = title) }
    }

    private fun updateTitle(title: String) {
        _uiState.update {
            it.copy(
                title = title.take(MAX_CHALLENGE_TITLE_LENGTH),
            )
        }
    }

    private fun updateDescription(description: String) {
        _uiState.update {
            it.copy(
                description = description.take(MAX_CHALLENGE_DESCRIPTION_LENGTH),
            )
        }
    }

    private fun updateStartDateTime(
        selectedDate: LocalDate,
        selectedHour: Int,
        selectedMinute: Int,
        isAm: Boolean,
    ) {
        val hour24Format = convertTo24HourFormat(selectedHour, isAm)
        val newDateTime = selectedDate.atTime(hour24Format, selectedMinute)

        if (newDateTime > LocalDateTime.now()) {
            _uiState.update { it.copy(startDateTime = LocalDateTime.now()) }
            sendEvent(AddChallengeUiEvent.StartDateTimeOutOfRange)
        } else {
            _uiState.update { it.copy(startDateTime = newDateTime) }
        }
    }

    private fun convertTo24HourFormat(hour: Int, isAm: Boolean): Int = when {
        hour == HOURS_PER_HALF_DAY && isAm -> MIDNIGHT_HOUR
        hour == HOURS_PER_HALF_DAY && !isAm -> NOON_HOUR
        isAm -> hour
        else -> hour + HOURS_PER_HALF_DAY
    }

    private fun updateTargetDays(targetDays: TargetDays) {
        if (targetDays is TargetDays.Fixed) {
            val adjustedDays = targetDays.days.coerceIn(MIN_CHALLENGE_TARGET_DAYS, MAX_CHALLENGE_TARGET_DAYS)
            _uiState.update { it.copy(targetDays = TargetDays.Fixed(adjustedDays)) }
        } else {
            _uiState.update { it.copy(targetDays = targetDays) }
        }
    }

    private fun updateMaxParticipants(maxParticipants: Int) {
        if (maxParticipants < MIN_CHALLENGE_TARGET_DAYS || maxParticipants > MAX_CHALLENGE_TARGET_DAYS) return
        _uiState.update { it.copy(maxParticipants = maxParticipants) }
    }

    private fun updateEnableRoomPassword(enableRoomPassword: Boolean) {
        _uiState.update { it.copy(enableRoomPassword = enableRoomPassword) }
    }

    private fun updateRoomPassword(roomPassword: String) {
        if (roomPassword.length > MAX_ROOM_PASSWORD_LENGTH) return
        _uiState.update { state -> state.copy(roomPassword = roomPassword.filter { !it.isWhitespace() }) }
    }

    private fun startChallenge(
        mode: Mode,
        category: Category,
        title: String,
        description: String,
        startDateTime: LocalDateTime,
        targetDays: TargetDays,
        languageCode: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingChallenge = true) }

            val adjustedStartDateTime = when {
                mode == Mode.FREE -> {
                    val now = LocalDateTime.now()
                    if (startDateTime.isAfter(now)) now else startDateTime
                }
                else -> null
            }

            val event = handleNetworkResult(
                result = challengeRepository.addChallenge(
                    category = category,
                    title = title,
                    description = description.ifBlank { title },
                    targetDays = if (mode == Mode.FREE) TargetDays.Infinite else targetDays,
                    startDateTime = adjustedStartDateTime,
                    languageCode = languageCode,
                ),
                onSuccess = { AddChallengeUiEvent.ChallengeStarted(it) },
                onNetworkError = { AddChallengeUiEvent.AddFailure.NetworkError },
                onHttpError = { AddChallengeUiEvent.AddFailure.UnknownError },
                onUnknownError = { AddChallengeUiEvent.AddFailure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isAddingChallenge = false) }
        }
    }

    private fun createWaitingRoom(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        maxParticipants: Int,
        enableRoomPassword: Boolean,
        roomPassword: String,
        languageCode: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingChallenge = true) }

            val event = handleNetworkResult(
                result = challengeRepository.addChallenge(
                    category = category,
                    title = title,
                    description = description.ifBlank { title },
                    targetDays = targetDays,
                    maxParticipants = maxParticipants,
                    roomPassword = if (enableRoomPassword) roomPassword else "",
                    languageCode = languageCode,
                ),
                onSuccess = { AddChallengeUiEvent.WaitingChallengeCreated(it) },
                onNetworkError = { AddChallengeUiEvent.AddFailure.NetworkError },
                onHttpError = { AddChallengeUiEvent.AddFailure.UnknownError },
                onUnknownError = { AddChallengeUiEvent.AddFailure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isAddingChallenge = false) }
        }
    }

    companion object {
        private const val MIN_CHALLENGES_FOR_AD = 1
    }
}
