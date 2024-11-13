package com.yjy.feature.editchallenge.model

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

sealed interface EditChallengeUiAction {
    data class OnEditCategory(
        val challengeId: Int,
        val category: Category,
    ) : EditChallengeUiAction

    data class OnEditTargetDays(
        val challengeId: Int,
        val targetDays: TargetDays,
    ) : EditChallengeUiAction

    data class OnEditTitle(
        val challengeId: Int,
        val title: String,
        val description: String,
    ) : EditChallengeUiAction
}
