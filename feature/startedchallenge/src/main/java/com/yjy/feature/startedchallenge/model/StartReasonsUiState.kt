package com.yjy.feature.startedchallenge.model

import com.yjy.model.challenge.StartReason

sealed interface StartReasonsUiState {
    data class Success(val reasons: List<StartReason>) : StartReasonsUiState
    data object Loading : StartReasonsUiState
}

fun StartReasonsUiState.reasonsOrEmpty(): List<StartReason> = when (this) {
    is StartReasonsUiState.Success -> reasons
    else -> emptyList()
}

fun StartReasonsUiState.isLoading(): Boolean = this is StartReasonsUiState.Loading
