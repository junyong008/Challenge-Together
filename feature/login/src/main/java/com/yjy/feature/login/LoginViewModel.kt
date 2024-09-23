package com.yjy.feature.login

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun login(email: String, password: String) {
        if (!isValidEmail(email)) {
            sendEvent(LoginUiEvent.LoginFailure.InvalidEmailFormat)
            return
        }

        if (password.isEmpty()) {
            sendEvent(LoginUiEvent.LoginFailure.EmptyPassword)
            return
        }

        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            val event = when (val result = authRepository.login(email, password)) {
                is NetworkResult.Success ->  LoginUiEvent.LoginSuccess
                is NetworkResult.Failure.NetworkError -> LoginUiEvent.LoginFailure.Unknown
                is NetworkResult.Failure.UnknownApiError -> LoginUiEvent.LoginFailure.Unknown
                is NetworkResult.Failure.HttpError -> {
                    when (result.code) {
                        404 -> LoginUiEvent.LoginFailure.UserNotFound
                        500 -> LoginUiEvent.LoginFailure.ServerError
                        else -> LoginUiEvent.LoginFailure.Unknown
                    }
                }
            }
            sendEvent(event)
            _loginUiState.value = LoginUiState.Idle
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}