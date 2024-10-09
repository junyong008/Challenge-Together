package com.yjy.feature.changepassword.model

sealed interface ChangePasswordUiEvent {
    data object CancelChangePassword : ChangePasswordUiEvent
    data object ChangeSuccess : ChangePasswordUiEvent

    sealed class Failure : ChangePasswordUiEvent {
        data object NetworkError : Failure()
        data object UnknownError : Failure()
    }
}