package com.yjy.feature.addchallenge

import androidx.lifecycle.viewModelScope
import com.yjy.common.core.base.BaseViewModel
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_DESCRIPTION_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TITLE_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_ROOM_PASSWORD_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_TARGET_DAYS
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.Mode
import com.yjy.model.challenge.TargetDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
) : BaseViewModel<AddChallengeUiState, AddChallengeUiEvent>(initialState = AddChallengeUiState()) {

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
            is AddChallengeUiAction.OnStartChallengeClick -> showAddConfirmDialog()
            is AddChallengeUiAction.OnCancelStartChallenge -> dismissAddConfirmDialog()
            is AddChallengeUiAction.OnCreateWaitingRoomClick -> showAddConfirmDialog()
            is AddChallengeUiAction.OnCancelCreateWaitingRoom -> dismissAddConfirmDialog()

            is AddChallengeUiAction.OnStartDateTimeUpdated -> updateStartDateTime(
                selectedDate = action.selectedDate,
                selectedHour = action.hour,
                selectedMinute = action.minute,
                isAm = action.isAm,
            )

            is AddChallengeUiAction.OnConfirmStartChallenge -> startChallenge(
                mode = action.mode,
                category = action.category,
                title = action.title,
                description = action.description,
                startDateTime = action.startDateTime,
                targetDays = action.targetDays,
            )

            is AddChallengeUiAction.OnConfirmCreateWaitingRoom -> createWaitingRoom(
                category = action.category,
                title = action.title,
                description = action.description,
                targetDays = action.targetDays,
                maxParticipants = action.maxParticipants,
                enableRoomPassword = action.enableRoomPassword,
                roomPassword = action.roomPassword,
            )
        }
    }

    private fun updateMode(mode: Mode) {
        updateState { copy(mode = mode) }
        sendEvent(AddChallengeUiEvent.ModeSelected)
    }

    private fun updateCategory(category: Category, title: String) {
        updateState { copy(category = category, title = title) }
    }

    private fun updateTitle(title: String) {
        if (title.length > MAX_CHALLENGE_TITLE_LENGTH) return
        updateState { copy(title = title) }
    }

    private fun updateDescription(description: String) {
        if (description.length > MAX_CHALLENGE_DESCRIPTION_LENGTH) return
        updateState { copy(description = description) }
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
            updateState { copy(startDateTime = LocalDateTime.now()) }
            sendEvent(AddChallengeUiEvent.StartDateTimeOutOfRange)
        } else {
            updateState { copy(startDateTime = newDateTime) }
        }
    }

    private fun convertTo24HourFormat(hour: Int, isAm: Boolean): Int = when {
        hour == 12 && isAm -> 0
        hour == 12 && !isAm -> 12
        isAm -> hour
        else -> hour + 12
    }

    private fun updateTargetDays(targetDays: TargetDays) {
        if (targetDays is TargetDays.Fixed) {
            val adjustedDays = targetDays.days.coerceIn(MIN_CHALLENGE_TARGET_DAYS, MAX_CHALLENGE_TARGET_DAYS)
            updateState { copy(targetDays = TargetDays.Fixed(adjustedDays)) }
        } else {
            updateState { copy(targetDays = targetDays) }
        }
    }

    private fun updateMaxParticipants(maxParticipants: Int) {
        if (maxParticipants < MIN_CHALLENGE_TARGET_DAYS || maxParticipants > MAX_CHALLENGE_TARGET_DAYS) return
        updateState { copy(maxParticipants = maxParticipants) }
    }

    private fun updateEnableRoomPassword(enableRoomPassword: Boolean) {
        updateState { copy(enableRoomPassword = enableRoomPassword) }
    }

    private fun updateRoomPassword(roomPassword: String) {
        if (roomPassword.length > MAX_ROOM_PASSWORD_LENGTH) return
        updateState { copy(roomPassword = roomPassword) }
    }

    private fun showAddConfirmDialog() {
        updateState { copy(shouldShowAddConfirmDialog = true) }
    }

    private fun dismissAddConfirmDialog() {
        updateState { copy(shouldShowAddConfirmDialog = false) }
    }

    private fun startChallenge(
        mode: Mode,
        category: Category,
        title: String,
        description: String,
        startDateTime: LocalDateTime,
        targetDays: TargetDays,
    ) {
        viewModelScope.launch {
            dismissAddConfirmDialog()
            updateState { copy(isAddingChallenge = true) }

            val event = handleNetworkResult(
                result = challengeRepository.addChallenge(
                    category = category,
                    title = title,
                    description = description.ifBlank { title },
                    targetDays = targetDays,
                    startDateTime = if (mode == Mode.FREE) startDateTime else null,
                ),
                onSuccess = { AddChallengeUiEvent.ChallengeAdded(it) },
                onNetworkError = { AddChallengeUiEvent.AddFailure.NetworkError },
                onHttpError = { AddChallengeUiEvent.AddFailure.UnknownError },
                onUnknownError = { AddChallengeUiEvent.AddFailure.UnknownError },
            )
            sendEvent(event)
            updateState { copy(isAddingChallenge = false) }
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
    ) {
        viewModelScope.launch {
            dismissAddConfirmDialog()
            updateState { copy(isAddingChallenge = true) }

            val event = handleNetworkResult(
                result = challengeRepository.addChallenge(
                    category = category,
                    title = title,
                    description = description.ifBlank { title },
                    targetDays = targetDays,
                    maxParticipants = maxParticipants,
                    roomPassword = if (enableRoomPassword) roomPassword else "",
                ),
                onSuccess = { AddChallengeUiEvent.ChallengeAdded(it) },
                onNetworkError = { AddChallengeUiEvent.AddFailure.NetworkError },
                onHttpError = { AddChallengeUiEvent.AddFailure.UnknownError },
                onUnknownError = { AddChallengeUiEvent.AddFailure.UnknownError },
            )
            sendEvent(event)
            updateState { copy(isAddingChallenge = false) }
        }
    }
}
