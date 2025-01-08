package com.yjy.feature.linkaccount.model

sealed interface LinkAccountUiEvent {
    data object Success : LinkAccountUiEvent
    sealed class Failure : LinkAccountUiEvent {
        data object AlreadyLinked : Failure()
        data object AlreadyRegistered : Failure()
        data object NetworkError : Failure()
        data object UnknownError : Failure()
    }
}
