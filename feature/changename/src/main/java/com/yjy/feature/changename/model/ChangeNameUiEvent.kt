package com.yjy.feature.changename.model

sealed interface ChangeNameUiEvent {
    data object ChangeSuccess : ChangeNameUiEvent

    sealed class Failure : ChangeNameUiEvent {
        data object DuplicatedName : Failure()
        data object NetworkError : Failure()
        data object UnknownError : Failure()
    }
}
