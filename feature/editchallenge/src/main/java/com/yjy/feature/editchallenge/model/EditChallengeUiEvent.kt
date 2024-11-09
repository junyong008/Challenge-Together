package com.yjy.feature.editchallenge.model

sealed interface EditChallengeUiEvent {
    data object EditSuccess : EditChallengeUiEvent
    data object EditFailure : EditChallengeUiEvent
}
