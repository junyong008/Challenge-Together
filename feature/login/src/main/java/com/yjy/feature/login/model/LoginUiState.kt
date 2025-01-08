package com.yjy.feature.login.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isValidEmailFormat: Boolean = true,
    val canTryLogin: Boolean = false,
)
