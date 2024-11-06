package com.yjy.feature.login

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.common.core.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.login.model.LoginUiAction
import com.yjy.feature.login.model.LoginUiEvent
import com.yjy.feature.login.model.LoginUiState
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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>()
    val uiEvent: Flow<LoginUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailUpdated -> updateEmail(action.email)
            is LoginUiAction.OnPasswordUpdated -> updatePassword(action.password)
            is LoginUiAction.OnEmailLoginClick -> emailLogin(action.email, action.password)
        }
    }

    private fun emailLogin(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val event = handleNetworkResult(
                result = authRepository.emailLogin(email, password),
                onSuccess = { LoginUiEvent.LoginSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.UNAUTHORIZED -> LoginUiEvent.LoginFailure.UserNotFound
                        else -> LoginUiEvent.LoginFailure.UnknownError
                    }
                },
                onNetworkError = { LoginUiEvent.LoginFailure.NetworkError },
                onUnknownError = { LoginUiEvent.LoginFailure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateEmail(email: String) {
        if (email.length > MAX_EMAIL_LENGTH) return
        _uiState.update {
            val isValidEmailFormat = EMAIL_ADDRESS.matcher(email).matches()

            it.copy(
                email = email,
                isValidEmailFormat = isValidEmailFormat,
                canTryLogin = canLogin(
                    email = email,
                    isValidEmailFormat = isValidEmailFormat,
                ),
            )
        }
    }

    private fun updatePassword(password: String) {
        if (password.length > MAX_PASSWORD_LENGTH) return
        _uiState.update {
            it.copy(
                password = password.filter { !it.isWhitespace() },
                canTryLogin = canLogin(password = password),
            )
        }
    }

    private fun canLogin(
        email: String = uiState.value.email,
        password: String = uiState.value.password,
        isValidEmailFormat: Boolean = uiState.value.isValidEmailFormat,
    ): Boolean {
        return email.isNotEmpty() &&
            password.isNotEmpty() &&
            isValidEmailFormat
    }
}
