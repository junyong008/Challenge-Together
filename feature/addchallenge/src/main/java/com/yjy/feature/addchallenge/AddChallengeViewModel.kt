package com.yjy.feature.addchallenge

import com.yjy.common.core.base.BaseViewModel
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_DESCRIPTION_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TITLE_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_TARGET_DAYS
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.Mode
import com.yjy.model.challenge.TargetDays
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddChallengeViewModel @Inject constructor(
) : BaseViewModel<AddChallengeUiState, AddChallengeUiEvent>(initialState = AddChallengeUiState()) {

    fun processAction(action: AddChallengeUiAction) {
        when (action) {
            is AddChallengeUiAction.OnSelectMode -> updateMode(action.mode)
            is AddChallengeUiAction.OnSelectCategory -> updateCategory(action.category, action.title)
            is AddChallengeUiAction.OnTitleUpdated -> updateTitle(action.title)
            is AddChallengeUiAction.OnDescriptionUpdated -> updateDescription(action.description)
            is AddChallengeUiAction.OnStartDateTimeUpdated -> updateStartDateTime(
                selectedDate = action.selectedDate,
                selectedHour = action.hour,
                selectedMinute = action.minute,
                isAm = action.isAm,
            )

            is AddChallengeUiAction.OnTargetDaysUpdated -> updateTargetDays(action.targetDays)
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
}
