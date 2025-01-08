package com.yjy.feature.findpassword.model

sealed interface FindPasswordUiAction {
    data class OnEmailUpdated(val email: String) : FindPasswordUiAction
    data class OnSendVerifyCodeClick(val email: String) : FindPasswordUiAction
    data class OnVerifyCodeUpdated(val code: String) : FindPasswordUiAction
}
