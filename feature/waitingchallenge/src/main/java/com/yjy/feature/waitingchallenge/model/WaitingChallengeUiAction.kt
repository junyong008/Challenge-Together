package com.yjy.feature.waitingchallenge.model

sealed interface WaitingChallengeUiAction {
    data class OnEnterPassword(val password: String) : WaitingChallengeUiAction
    data object OnDismissPasswordDialog : WaitingChallengeUiAction
    data object OnPasswordCopy : WaitingChallengeUiAction
    data object OnRefreshClick : WaitingChallengeUiAction
    data class OnDeleteClick(val challengeId: Int) : WaitingChallengeUiAction
    data class OnStartClick(val challengeId: Int) : WaitingChallengeUiAction
    data class OnJoinClick(val challengeId: Int) : WaitingChallengeUiAction
    data class OnLeaveClick(val challengeId: Int) : WaitingChallengeUiAction
}
