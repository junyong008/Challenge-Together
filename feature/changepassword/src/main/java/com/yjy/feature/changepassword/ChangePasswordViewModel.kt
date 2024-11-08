package com.yjy.feature.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.changepassword.model.ChangePasswordUiAction
import com.yjy.feature.changepassword.model.ChangePasswordUiEvent
import com.yjy.feature.changepassword.model.ChangePasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ChangePasswordUiState> = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<ChangePasswordUiEvent>()
    val uiEvent: Flow<ChangePasswordUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: ChangePasswordUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: ChangePasswordUiAction) {
        when (action) {
            is ChangePasswordUiAction.OnConfirmChange -> changePassword(action.password)
            is ChangePasswordUiAction.OnPasswordUpdated -> updatePassword(action.password)
        }
    }

    private fun changePassword(password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true) }

            val event = handleNetworkResult(
                result = authRepository.changePassword(password),
                onSuccess = { ChangePasswordUiEvent.ChangeSuccess },
                onHttpError = { ChangePasswordUiEvent.Failure.UnknownError },
                onNetworkError = { ChangePasswordUiEvent.Failure.NetworkError },
                onUnknownError = { ChangePasswordUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isChangingPassword = false) }
        }
    }

    private fun updatePassword(password: String) {
        if (password.length > MAX_PASSWORD_LENGTH) return
        _uiState.update {
            val isPasswordLongEnough = password.length >= MIN_PASSWORD_LENGTH
            val isPasswordContainNumber = password.any { it.isDigit() }

            it.copy(
                password = password,
                isPasswordLongEnough = isPasswordLongEnough,
                isPasswordContainNumber = isPasswordContainNumber,
            )
        }
    }
}
