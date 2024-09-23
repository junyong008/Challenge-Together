package com.yjy.feature.login

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
}