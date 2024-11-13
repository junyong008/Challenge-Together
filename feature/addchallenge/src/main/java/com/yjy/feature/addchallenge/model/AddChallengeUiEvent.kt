package com.yjy.feature.addchallenge.model

sealed interface AddChallengeUiEvent {
    data object ModeSelected : AddChallengeUiEvent
    data object StartDateTimeOutOfRange : AddChallengeUiEvent
    data class ChallengeStarted(val challengeId: Int) : AddChallengeUiEvent
    data class WaitingChallengeCreated(val challengeId: Int) : AddChallengeUiEvent

    sealed class AddFailure : AddChallengeUiEvent {
        data object NetworkError : AddFailure()
        data object UnknownError : AddFailure()
    }
}
