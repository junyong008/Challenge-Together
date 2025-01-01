package com.yjy.feature.applock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.applock.model.AppLockUiAction
import com.yjy.feature.applock.model.AppLockUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLockViewModel @Inject constructor(
    userRepository: UserRepository,
    private val appLockRepository: AppLockRepository,
) : ViewModel() {

    val isPremium = userRepository.isPremium
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val isPinSet = appLockRepository.isPinSet
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val isBiometricEnabled = appLockRepository.isBiometricEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val shouldHideWidgetContents = appLockRepository.shouldHideWidgetContents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val shouldHideNotificationContents = appLockRepository.shouldHideNotificationContents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    private val _uiEvent = Channel<AppLockUiEvent>()
    val uiEvent: Flow<AppLockUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: AppLockUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    fun processAction(action: AppLockUiAction) {
        when (action) {
            AppLockUiAction.OnRemovePin -> removePin()
            AppLockUiAction.OnPinMismatch -> sendEvent(AppLockUiEvent.OnPinMismatch)
            is AppLockUiAction.OnSetPin -> setPin(action.pin)
            is AppLockUiAction.OnValidatePin -> validatePin(action.pin)
            is AppLockUiAction.OnToggleBiometric -> toggleBiometric(action.enabled)
            is AppLockUiAction.OnToggleHideWidget -> toggleHideWidget(action.enabled)
            is AppLockUiAction.OnToggleHideNotification -> toggleHideNotification(action.enabled)
        }
    }

    private fun removePin() {
        viewModelScope.launch { appLockRepository.removePin() }
    }

    private fun setPin(pin: String) {
        viewModelScope.launch {
            appLockRepository.savePin(pin)
            sendEvent(AppLockUiEvent.OnPinSetSuccess)
        }
    }

    private fun validatePin(pin: String) {
        viewModelScope.launch {
            val isValid = appLockRepository.validatePin(pin)
            if (isValid) {
                sendEvent(AppLockUiEvent.OnPinValidationSuccess)
            } else {
                sendEvent(AppLockUiEvent.OnPinValidationFailure)
            }
        }
    }

    private fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch { appLockRepository.setBiometricEnabled(!enabled) }
    }

    private fun toggleHideWidget(enabled: Boolean) {
        viewModelScope.launch { appLockRepository.setHideWidgetContents(!enabled) }
    }

    private fun toggleHideNotification(enabled: Boolean) {
        viewModelScope.launch { appLockRepository.setHideNotificationContents(!enabled) }
    }
}
