package com.yjy.feature.editchallenge.model

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

sealed interface EditChallengeUiAction {
    data class OnEditCategory(
        val challengeId: String,
        val category: Category,
    ) : EditChallengeUiAction

    data class OnEditTargetDays(
        val challengeId: String,
        val targetDays: TargetDays,
    ) : EditChallengeUiAction

    data class OnEditTitle(
        val challengeId: String,
        val title: String,
        val description: String,
    ) : EditChallengeUiAction
}
