package com.yjy.feature.login

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.core.common.network.HttpStatusCodes
import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
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
            is LoginUiAction.OnFindPasswordClick -> {}
            is LoginUiAction.OnSignUpClick -> {}
            is LoginUiAction.OnKakaoLoginClick -> {}
            is LoginUiAction.OnGoogleLoginClick -> {}
            is LoginUiAction.OnNaverLoginClick -> {}
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
            it.copy(
                email = email,
                isValidEmailFormat = isValidEmail(email),
                canTryLogin = canLogin(email, it.password),
            )
        }
    }

    private fun updatePassword(password: String) {
        if (password.length > MAX_PASSWORD_LENGTH) return
        _uiState.update {
            it.copy(
                password = password,
                canTryLogin = canLogin(it.email, password),
            )
        }
    }

    private fun canLogin(email: String, password: String): Boolean =
        email.isNotEmpty() && password.isNotEmpty() && isValidEmail(email)

    private fun isValidEmail(email: String): Boolean = EMAIL_ADDRESS.matcher(email).matches()

    private fun sendEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    companion object {
        private const val MAX_EMAIL_LENGTH = 25
        private const val MAX_PASSWORD_LENGTH = 20
    }
}
