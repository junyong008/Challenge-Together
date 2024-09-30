package com.yjy.feature.signup.model

sealed interface SignUpUiAction {
    data object OnBackClick : SignUpUiAction
    data class OnEmailUpdated(val email: String) : SignUpUiAction
    data class OnPasswordUpdated(val password: String) : SignUpUiAction
    data object OnContinueToNicknameClick : SignUpUiAction
}
