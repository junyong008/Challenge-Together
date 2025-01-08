package com.yjy.feature.waitingchallenge.model

import com.yjy.model.challenge.DetailedWaitingChallenge

sealed interface ChallengeDetailUiState {
    data object Loading : ChallengeDetailUiState
    data class Success(val challenge: DetailedWaitingChallenge) : ChallengeDetailUiState
}

fun ChallengeDetailUiState.challengeOrNull(): DetailedWaitingChallenge? = when (this) {
    ChallengeDetailUiState.Loading -> null
    is ChallengeDetailUiState.Success -> challenge
}

fun ChallengeDetailUiState.isLoading(): Boolean = this is ChallengeDetailUiState.Loading
