package com.yjy.feature.login.model

sealed interface LoginUiEvent {
    data object LoginSuccess : LoginUiEvent

    sealed class LoginFailure : LoginUiEvent {
        data object UserNotFound : LoginFailure()
        data object NetworkError : LoginFailure()
        data object UnknownError : LoginFailure()
    }
}
