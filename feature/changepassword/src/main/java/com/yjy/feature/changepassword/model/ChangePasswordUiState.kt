package com.yjy.feature.changepassword.model

data class ChangePasswordUiState(
    val password: String = "",
    val isPasswordLongEnough: Boolean = false,
    val isPasswordContainNumber: Boolean = false,
    val isChangingPassword: Boolean = false,
)
