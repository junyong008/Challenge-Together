package com.yjy.feature.addchallenge.model

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface AddChallengeUiAction {
    data class OnSelectMode(val mode: Mode) : AddChallengeUiAction
    data class OnSelectCategory(val category: Category, val title: String) : AddChallengeUiAction
    data class OnTitleUpdated(val title: String) : AddChallengeUiAction
    data class OnDescriptionUpdated(val description: String) : AddChallengeUiAction
    data class OnTargetDaysUpdated(val targetDays: TargetDays) : AddChallengeUiAction
    data class OnMaxParticipantsUpdated(val maxParticipants: Int) : AddChallengeUiAction
    data class OnEnableRoomPasswordUpdated(val enableRoomPassword: Boolean) : AddChallengeUiAction
    data class OnRoomPasswordUpdated(val roomPassword: String) : AddChallengeUiAction

    data class OnStartDateTimeUpdated(
        val selectedDate: LocalDate,
        val hour: Int,
        val minute: Int,
        val isAm: Boolean,
    ) : AddChallengeUiAction

    data class OnStartChallenge(
        val mode: Mode,
        val category: Category,
        val title: String,
        val description: String,
        val startDateTime: LocalDateTime,
        val targetDays: TargetDays,
        val languageCode: String,
    ) : AddChallengeUiAction

    data class OnCreateWaitingRoom(
        val category: Category,
        val title: String,
        val description: String,
        val targetDays: TargetDays,
        val maxParticipants: Int,
        val enableRoomPassword: Boolean,
        val roomPassword: String,
        val languageCode: String,
    ) : AddChallengeUiAction
}
