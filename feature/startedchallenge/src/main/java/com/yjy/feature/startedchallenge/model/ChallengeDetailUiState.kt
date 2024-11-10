package com.yjy.feature.startedchallenge.model

import com.yjy.model.challenge.DetailedStartedChallenge

sealed interface ChallengeDetailUiState {
    data object Loading : ChallengeDetailUiState
    data class Success(val challenge: DetailedStartedChallenge) : ChallengeDetailUiState
}

fun ChallengeDetailUiState.challengeOrNull(): DetailedStartedChallenge? = when (this) {
    is ChallengeDetailUiState.Loading -> null
    is ChallengeDetailUiState.Success -> challenge
}

fun ChallengeDetailUiState.isLoading(): Boolean = this is ChallengeDetailUiState.Loading
