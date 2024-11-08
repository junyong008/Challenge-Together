package com.yjy.feature.startedchallenge.model

sealed interface StartedChallengeUiAction {
    data class OnResetClick(val challengeId: String, val memo: String) : StartedChallengeUiAction
    data class OnDeleteChallengeClick(val challengeId: String) : StartedChallengeUiAction
}
