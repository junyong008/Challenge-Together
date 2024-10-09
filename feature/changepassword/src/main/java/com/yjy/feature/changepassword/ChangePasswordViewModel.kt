package com.yjy.feature.changepassword

import androidx.lifecycle.viewModelScope
import com.yjy.core.common.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.core.common.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.core.common.network.handleNetworkResult
import com.yjy.core.common.ui.BaseViewModel
import com.yjy.core.data.repository.AuthRepository
import com.yjy.feature.changepassword.model.ChangePasswordUiAction
import com.yjy.feature.changepassword.model.ChangePasswordUiEvent
import com.yjy.feature.changepassword.model.ChangePasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<ChangePasswordUiState, ChangePasswordUiEvent>(initialState = ChangePasswordUiState()) {

    fun processAction(action: ChangePasswordUiAction) {
        when (action) {
            is ChangePasswordUiAction.OnBackClick -> showExitConfirmDialog()
            is ChangePasswordUiAction.OnCancelExit -> dismissExitConfirmDialog()
            is ChangePasswordUiAction.OnConfirmExit -> confirmExit()
            is ChangePasswordUiAction.OnChangeClick -> showChangeConfirmDialog()
            is ChangePasswordUiAction.OnCancelChange -> dismissChangeConfirmDialog()
            is ChangePasswordUiAction.OnConfirmChange -> changePassword(action.password)
            is ChangePasswordUiAction.OnPasswordUpdated -> updatePassword(action.password)
        }
    }

    private fun showExitConfirmDialog() {
        updateState { copy(shouldShowExitConfirmDialog = true) }
    }

    private fun dismissExitConfirmDialog() {
        updateState { copy(shouldShowExitConfirmDialog = false) }
    }

    private fun confirmExit() {
        viewModelScope.launch {
            dismissExitConfirmDialog()
            delay(10)
            sendEvent(ChangePasswordUiEvent.CancelChangePassword)
        }
    }

    private fun showChangeConfirmDialog() {
        updateState { copy(shouldShowChangeConfirmDialog = true) }
    }

    private fun dismissChangeConfirmDialog() {
        updateState { copy(shouldShowChangeConfirmDialog = false) }
    }

    private fun changePassword(password: String) {
        viewModelScope.launch {
            updateState {
                copy(
                    isChangingPassword = true,
                    shouldShowChangeConfirmDialog = false,
                )
            }

            val event = handleNetworkResult(
                result = authRepository.changePassword(password),
                onSuccess = { ChangePasswordUiEvent.ChangeSuccess },
                onHttpError = { ChangePasswordUiEvent.Failure.UnknownError },
                onNetworkError = { ChangePasswordUiEvent.Failure.NetworkError },
                onUnknownError = { ChangePasswordUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            updateState { copy(isChangingPassword = false) }
        }
    }

    private fun updatePassword(password: String) {
        if (password.length > MAX_PASSWORD_LENGTH) return
        updateState {
            val isPasswordLongEnough = password.length >= MIN_PASSWORD_LENGTH
            val isPasswordContainNumber = password.any { it.isDigit() }

            copy(
                password = password,
                isPasswordLongEnough = isPasswordLongEnough,
                isPasswordContainNumber = isPasswordContainNumber,
            )
        }
    }
}