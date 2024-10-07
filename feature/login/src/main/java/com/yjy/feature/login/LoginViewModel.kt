package com.yjy.feature.login

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.core.common.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.core.common.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.core.common.network.HttpStatusCodes
import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
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

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>()
    val uiEvent: Flow<LoginUiEvent> = _uiEvent.receiveAsFlow()

    fun processAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailUpdated -> updateEmail(action.email)
            is LoginUiAction.OnPasswordUpdated -> updatePassword(action.password)
            is LoginUiAction.OnLoginClick -> login(action.email, action.password)
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val event = when (val result = authRepository.login(email, password)) {
                is NetworkResult.Success -> LoginUiEvent.LoginSuccess
                is NetworkResult.Failure.HttpError -> when (result.code) {
                    HttpStatusCodes.NOT_FOUND -> LoginUiEvent.LoginFailure.UserNotFound
                    else -> LoginUiEvent.LoginFailure.Error
                }

                is NetworkResult.Failure -> LoginUiEvent.LoginFailure.Error
            }
            sendEvent(event)

            _uiState.update {
                it.copy(isLoading = false)
            }
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
                password = password,
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

    private fun sendEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
