package com.yjy.feature.home.model

sealed interface ChallengeSyncUiState {
    data object Success : ChallengeSyncUiState
    data object Error : ChallengeSyncUiState

    sealed interface Loading : ChallengeSyncUiState {
        data object Initial : Loading
        data object Manual : Loading
        data object TimeSync : Loading
    }
}

fun ChallengeSyncUiState.isSuccess(): Boolean = this is ChallengeSyncUiState.Success
fun ChallengeSyncUiState.isError(): Boolean = this is ChallengeSyncUiState.Error
fun ChallengeSyncUiState.isInitialLoading(): Boolean = this is ChallengeSyncUiState.Loading.Initial
fun ChallengeSyncUiState.isTimeSyncLoading(): Boolean = this is ChallengeSyncUiState.Loading.TimeSync
