package com.yjy.feature.signup.model

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val isNicknameLengthValid: Boolean = true,
    val isNicknameHasOnlyConsonantOrVowel: Boolean = false,
    val isValidatingEmail: Boolean = false,
    val isValidEmailFormat: Boolean = false,
    val isPasswordLongEnough: Boolean = false,
    val isPasswordContainNumber: Boolean = false,
    val canTryContinueToNickname: Boolean = false,
    val canTryStart: Boolean = false,
    val isSigningUp: Boolean = false,
)
