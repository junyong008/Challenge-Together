package com.yjy.feature.home.model

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder

sealed interface HomeUiAction {
    data object OnScreenLoad : HomeUiAction
    data object OnRetryClick : HomeUiAction
    data object OnCloseCompletedChallengeNotification : HomeUiAction
    data object OnDismissTierUpAnimation : HomeUiAction
    data class OnCategorySelect(val category: Category) : HomeUiAction
    data class OnSortOrderSelect(val sortOrder: SortOrder) : HomeUiAction
}
