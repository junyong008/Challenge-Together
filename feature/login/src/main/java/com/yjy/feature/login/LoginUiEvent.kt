package com.yjy.feature.login

sealed interface LoginUiEvent {
    data object LoginSuccess: LoginUiEvent

    sealed class LoginFailure : LoginUiEvent {
        data object UserNotFound : LoginFailure()
        data object Error : LoginFailure()
    }
}