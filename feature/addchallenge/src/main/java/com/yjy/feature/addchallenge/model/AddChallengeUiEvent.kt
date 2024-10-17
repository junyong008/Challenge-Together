package com.yjy.feature.addchallenge.model

sealed interface AddChallengeUiEvent {
    data object ModeSelected : AddChallengeUiEvent
    data object StartDateTimeOutOfRange : AddChallengeUiEvent
}