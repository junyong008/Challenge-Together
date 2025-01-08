package com.yjy.feature.applock.model

sealed interface AppLockUiAction {
    data object OnRemovePin : AppLockUiAction
    data object OnPinMismatch : AppLockUiAction
    data class OnSetPin(val pin: String) : AppLockUiAction
    data class OnValidatePin(val pin: String) : AppLockUiAction
    data class OnToggleBiometric(val enabled: Boolean) : AppLockUiAction
    data class OnToggleHideWidget(val enabled: Boolean) : AppLockUiAction
    data class OnToggleHideNotification(val enabled: Boolean) : AppLockUiAction
}
