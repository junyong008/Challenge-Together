package com.yjy.feature.login.model

sealed interface LoginUiAction {
    data class OnEmailUpdated(val email: String) : LoginUiAction
    data class OnPasswordUpdated(val password: String) : LoginUiAction
    data class OnLoginClick(val email: String, val password: String) : LoginUiAction
}
