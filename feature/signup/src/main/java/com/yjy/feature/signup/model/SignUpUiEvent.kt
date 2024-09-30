package com.yjy.feature.signup.model

sealed interface SignUpUiEvent {
    data object NavigateBack : SignUpUiEvent
    data object NavigateToSignUpNickname : SignUpUiEvent
}
