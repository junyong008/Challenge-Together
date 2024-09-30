package com.yjy.feature.signup

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.core.common.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.core.common.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.core.common.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.feature.signup.model.SignUpUiAction
import com.yjy.feature.signup.model.SignUpUiEvent
import com.yjy.feature.signup.model.SignUpUiState
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
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SignUpUiEvent>()
    val uiEvent: Flow<SignUpUiEvent> = _uiEvent.receiveAsFlow()

    fun processAction(action: SignUpUiAction) {
        when (action) {
            is SignUpUiAction.OnBackClick -> navigateBack()
            is SignUpUiAction.OnContinueToNicknameClick -> {}
            is SignUpUiAction.OnEmailUpdated -> updateEmail(action.email)
            is SignUpUiAction.OnPasswordUpdated -> updatePassword(action.password)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEvent.send(SignUpUiEvent.NavigateBack)
        }
    }

    private fun updateEmail(email: String) {
        if (email.length > MAX_EMAIL_LENGTH) return
        _uiState.update {
            val isValidEmailFormat = EMAIL_ADDRESS.matcher(email).matches()

            it.copy(
                email = email,
                isValidEmailFormat = isValidEmailFormat,
                canTryContinueToNickname = canContinue(
                    email = email,
                    isValidEmailFormat = isValidEmailFormat,
                ),
            )
        }
    }

    private fun updatePassword(password: String) {
        if (password.length > MAX_PASSWORD_LENGTH) return
        _uiState.update { currentState ->
            val isPasswordLongEnough = password.length >= MIN_PASSWORD_LENGTH
            val isPasswordContainNumber = password.any { it.isDigit() }

            currentState.copy(
                password = password,
                isPasswordLongEnough = isPasswordLongEnough,
                isPasswordContainNumber = isPasswordContainNumber,
                canTryContinueToNickname = canContinue(
                    password = password,
                    isPasswordLongEnough = isPasswordLongEnough,
                    isPasswordContainNumber = isPasswordContainNumber,
                ),
            )
        }
    }

    private fun canContinue(
        email: String = uiState.value.email,
        password: String = uiState.value.password,
        isValidEmailFormat: Boolean = uiState.value.isValidEmailFormat,
        isPasswordLongEnough: Boolean = uiState.value.isPasswordLongEnough,
        isPasswordContainNumber: Boolean = uiState.value.isPasswordContainNumber,
    ): Boolean =
        email.isNotEmpty() &&
            password.isNotEmpty() &&
            isValidEmailFormat &&
            isPasswordLongEnough &&
            isPasswordContainNumber
}
