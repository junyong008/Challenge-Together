package com.yjy.feature.applock.model

sealed interface AppLockUiEvent {
    data object OnPinSetSuccess : AppLockUiEvent
    data object OnPinMismatch : AppLockUiEvent
    data object OnPinValidationSuccess : AppLockUiEvent
    data object OnPinValidationFailure : AppLockUiEvent
}
