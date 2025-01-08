package com.yjy.feature.my.model

sealed interface MyUiEvent {
    data object FindEmailAppFailure : MyUiEvent
    data object SendEmailFailure : MyUiEvent
    data object Logout : MyUiEvent
}
