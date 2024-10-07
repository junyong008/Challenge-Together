package com.yjy.feature.signup.model

sealed interface SignUpUiEvent {
    data object EmailPasswordVerified : SignUpUiEvent

    sealed class EmailPasswordVerifyFailure : SignUpUiEvent {
        data object DuplicatedEmail : EmailPasswordVerifyFailure()
        data object UnknownError : EmailPasswordVerifyFailure()
    }

    data object SignUpSuccess : SignUpUiEvent

    sealed class SignUpFailure : SignUpUiEvent {
        data object DuplicatedNickname : SignUpFailure()
        data object UnknownError : SignUpFailure()
    }
}
