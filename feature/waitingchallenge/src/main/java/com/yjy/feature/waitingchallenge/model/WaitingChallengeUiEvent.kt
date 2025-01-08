package com.yjy.feature.waitingchallenge.model

sealed interface WaitingChallengeUiEvent {
    data object PasswordInputCanceled : WaitingChallengeUiEvent
    data object PasswordCopied : WaitingChallengeUiEvent
    data object DeleteSuccess : WaitingChallengeUiEvent
    data object DeleteFailure : WaitingChallengeUiEvent
    data class StartSuccess(val challengeId: Int) : WaitingChallengeUiEvent
    data object StartFailure : WaitingChallengeUiEvent

    sealed class LoadFailure : WaitingChallengeUiEvent {
        data object PasswordIncorrect : LoadFailure()
        data object AlreadyStarted : LoadFailure()
        data object NotFound : LoadFailure()
        data object Network : LoadFailure()
        data object Unknown : LoadFailure()
    }

    sealed class JoinFailure : WaitingChallengeUiEvent {
        data object Full : JoinFailure()
        data object Unknown : JoinFailure()
    }

    sealed class LeaveFailure : WaitingChallengeUiEvent {
        data object Unknown : LeaveFailure()
    }
}
