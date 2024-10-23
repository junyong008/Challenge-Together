package com.yjy.feature.addchallenge.model

sealed interface AddChallengeUiEvent {
    data object ModeSelected : AddChallengeUiEvent
    data object StartDateTimeOutOfRange : AddChallengeUiEvent
    data class ChallengeAdded(val challengeId: String) : AddChallengeUiEvent

    sealed class AddFailure : AddChallengeUiEvent {
        data object NetworkError : AddFailure()
        data object UnknownError : AddFailure()
    }
}