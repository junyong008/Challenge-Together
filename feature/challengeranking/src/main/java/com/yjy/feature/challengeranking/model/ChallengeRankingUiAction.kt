package com.yjy.feature.challengeranking.model

sealed interface ChallengeRankingUiAction {
    data object RetryOnError : ChallengeRankingUiAction
    data class OnForceRemoveClick(val memberId: Int) : ChallengeRankingUiAction
}
