package com.yjy.feature.login

sealed interface LoginUiEvent {
    data object LoginSuccess: LoginUiEvent

    sealed class LoginFailure : LoginUiEvent {
        data object UserNotFound : LoginFailure()
        data object ServerError : LoginFailure()
        data object InvalidEmailFormat : LoginFailure()
        data object EmptyPassword : LoginFailure()
        data object Unknown : LoginFailure()
    }
}