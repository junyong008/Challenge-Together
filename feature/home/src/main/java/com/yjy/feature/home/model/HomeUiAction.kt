package com.yjy.feature.home.model

sealed interface HomeUiAction {
    data object OnScreenLoad : HomeUiAction
    data object OnRetryClick : HomeUiAction
    data object OnCloseCompletedChallengeNotification : HomeUiAction
}
