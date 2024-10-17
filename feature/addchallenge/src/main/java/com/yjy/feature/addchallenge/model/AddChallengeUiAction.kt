package com.yjy.feature.addchallenge.model

import com.yjy.model.challenge.Category
import com.yjy.model.challenge.Mode
import com.yjy.model.challenge.TargetDays
import java.time.LocalDate

sealed interface AddChallengeUiAction {
    data class OnSelectMode(val mode: Mode) : AddChallengeUiAction
    data class OnSelectCategory(val category: Category, val title: String) : AddChallengeUiAction
    data class OnTitleUpdated(val title: String) : AddChallengeUiAction
    data class OnDescriptionUpdated(val description: String) : AddChallengeUiAction
    data class OnStartDateTimeUpdated(
        val selectedDate: LocalDate,
        val hour: Int,
        val minute: Int,
        val isAm: Boolean
    ) : AddChallengeUiAction

    data class OnTargetDaysUpdated(val targetDays: TargetDays) : AddChallengeUiAction
}