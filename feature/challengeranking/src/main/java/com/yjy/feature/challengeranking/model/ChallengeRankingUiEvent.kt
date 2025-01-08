package com.yjy.feature.challengeranking.model

sealed interface ChallengeRankingUiEvent {
    data object ForceRemoveSuccess : ChallengeRankingUiEvent

    sealed class ForceRemoveFailed : ChallengeRankingUiEvent {
        data object UserReConnected : ForceRemoveFailed()
        data object NetworkError : ForceRemoveFailed()
        data object UnknownError : ForceRemoveFailed()
    }
}
