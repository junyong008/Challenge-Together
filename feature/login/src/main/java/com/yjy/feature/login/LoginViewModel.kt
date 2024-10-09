package com.yjy.feature.login

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.viewModelScope
import com.yjy.core.common.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.core.common.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.core.common.network.HttpStatusCodes
import com.yjy.core.common.network.handleNetworkResult
import com.yjy.core.common.ui.BaseViewModel
import com.yjy.core.data.repository.AuthRepository
import com.yjy.feature.login.model.LoginUiAction
import com.yjy.feature.login.model.LoginUiEvent
import com.yjy.feature.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<LoginUiState, LoginUiEvent>(initialState = LoginUiState()) {

    fun processAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailUpdated -> updateEmail(action.email)
            is LoginUiAction.OnPasswordUpdated -> updatePassword(action.password)
            is LoginUiAction.OnEmailLoginClick -> emailLogin(action.email, action.password)
        }
    }

    private fun emailLogin(email: String, password: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

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
            updateState { copy(isLoading = false) }
        }
    }

    private fun updateEmail(email: String) {
        if (email.length > MAX_EMAIL_LENGTH) return
        updateState {
            val isValidEmailFormat = EMAIL_ADDRESS.matcher(email).matches()

            copy(
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
        updateState {
            copy(
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
}
