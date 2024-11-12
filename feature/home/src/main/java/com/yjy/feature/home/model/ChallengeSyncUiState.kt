package com.yjy.feature.home.model

sealed interface ChallengeSyncUiState {
    data object Success : ChallengeSyncUiState
    data object Error : ChallengeSyncUiState
    data object Loading : ChallengeSyncUiState
}

fun ChallengeSyncUiState.isSuccess(): Boolean = this is ChallengeSyncUiState.Success
fun ChallengeSyncUiState.isError(): Boolean = this is ChallengeSyncUiState.Error
fun ChallengeSyncUiState.isLoading(): Boolean = this is ChallengeSyncUiState.Loading
