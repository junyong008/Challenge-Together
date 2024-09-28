package com.yjy.feature.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val canTryLogin: Boolean = false,
    val isValidEmailFormat: Boolean = true,
    val email: String = "",
    val password: String = "",
)