package com.yjy.feature.deleteaccount.model

sealed interface DeleteAccountUiEvent {
    data object Success : DeleteAccountUiEvent
    data object Failure : DeleteAccountUiEvent
}
