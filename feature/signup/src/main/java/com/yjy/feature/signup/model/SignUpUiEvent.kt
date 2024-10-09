package com.yjy.feature.signup.model

sealed interface SignUpUiEvent {
    data object EmailPasswordVerified : SignUpUiEvent
    data object DuplicatedEmail: SignUpUiEvent
    data object SignUpSuccess : SignUpUiEvent
    data object DuplicatedNickname : SignUpUiEvent

    sealed class Failure : SignUpUiEvent {
        data object NetworkError : Failure()
        data object UnknownError : Failure()
    }
}
