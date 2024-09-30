package com.yjy.feature.signup.model

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val isValidEmailFormat: Boolean = false,
    val isPasswordLongEnough: Boolean = false,
    val isPasswordContainNumber: Boolean = false,
    val canTryContinueToNickname: Boolean = false,
)
