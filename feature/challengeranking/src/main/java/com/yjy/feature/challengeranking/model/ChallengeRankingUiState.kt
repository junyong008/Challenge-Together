package com.yjy.feature.challengeranking.model

import com.yjy.model.challenge.ChallengeRank

sealed interface ChallengeRankingUiState {
    data object Error : ChallengeRankingUiState
    data object Loading : ChallengeRankingUiState
    data class Success(val challengeRanks: List<ChallengeRank>) : ChallengeRankingUiState
}

fun ChallengeRankingUiState.challengeRanksOrNull(): List<ChallengeRank>? = when (this) {
    is ChallengeRankingUiState.Error -> null
    is ChallengeRankingUiState.Loading -> null
    is ChallengeRankingUiState.Success -> challengeRanks
}
