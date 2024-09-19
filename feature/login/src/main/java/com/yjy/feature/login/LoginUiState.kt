package com.yjy.feature.login

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState

    sealed class Error : LoginUiState {
        data object UserNotFound : Error()
        data object ServerError : Error()
        data object InvalidEmailFormat : Error()
        data object EmptyPassword : Error()
        data object Unknown : Error()
    }
}