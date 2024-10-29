package com.yjy.feature.home.model

import com.yjy.model.challenge.core.Category

sealed interface HomeUiAction {
    data object OnScreenLoad : HomeUiAction
    data object OnRetryClick : HomeUiAction
    data object OnCloseCompletedChallengeNotification : HomeUiAction
    data object OnDismissTierUpAnimation : HomeUiAction
    data class OnCategorySelect(val category: Category) : HomeUiAction
}
