package com.yjy.feature.home.model

sealed interface TimeSyncUiState {
    data object Success : TimeSyncUiState
    data object Error : TimeSyncUiState
    data object Loading : TimeSyncUiState
}

fun TimeSyncUiState.isSuccess(): Boolean = this is TimeSyncUiState.Success
fun TimeSyncUiState.isError(): Boolean = this is TimeSyncUiState.Error
fun TimeSyncUiState.isLoading(): Boolean = this is TimeSyncUiState.Loading
