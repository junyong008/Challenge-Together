package com.yjy.feature.my.model

sealed interface MyUiAction {
    data object OnRetryClick : MyUiAction
    data object OnLogoutClick : MyUiAction
    data object OnFindEmailAppFailure : MyUiAction
    data object OnSendEmailFailure : MyUiAction
}
