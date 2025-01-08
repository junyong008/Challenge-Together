package com.yjy.feature.startedchallenge.model

sealed interface StartedChallengeUiAction {
    data class OnResetClick(val challengeId: Int, val memo: String) : StartedChallengeUiAction
    data class OnDeleteChallengeClick(val challengeId: Int) : StartedChallengeUiAction
}
