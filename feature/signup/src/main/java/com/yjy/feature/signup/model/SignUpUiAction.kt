package com.yjy.feature.signup.model

sealed interface SignUpUiAction {
    data class OnEmailUpdated(val email: String) : SignUpUiAction
    data class OnPasswordUpdated(val password: String) : SignUpUiAction
    data class OnNicknameUpdated(val nickname: String) : SignUpUiAction
    data class OnEmailPasswordContinueClick(val email: String) : SignUpUiAction
    data class OnStartClick(
        val nickname: String,
        val email: String,
        val password: String,
        val kakaoId: String,
        val googleId: String,
        val naverId: String,
        val guestId: String,
    ) : SignUpUiAction
}
