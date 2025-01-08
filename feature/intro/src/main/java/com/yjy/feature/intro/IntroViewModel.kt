package com.yjy.feature.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.intro.model.IntroUiAction
import com.yjy.feature.intro.model.IntroUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isTryingLogin = MutableStateFlow(false)
    val isTryingLogin: StateFlow<Boolean> = _isTryingLogin.asStateFlow()

    private val _uiEvent = Channel<IntroUiEvent>()
    val uiEvent: Flow<IntroUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: IntroUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: IntroUiAction) {
        when (action) {
            is IntroUiAction.OnKakaoLoginClick -> kakaoLogin(action.kakaoId)
            is IntroUiAction.OnGoogleLoginClick -> googleLogin(action.googleId)
            is IntroUiAction.OnNaverLoginClick -> naverLogin(action.naverId)
            is IntroUiAction.OnGuestLoginClick -> guestLogin(action.guestId)
            IntroUiAction.OnLoginFailure -> sendEvent(IntroUiEvent.LoginFailure)
        }
    }

    private fun kakaoLogin(kakaoId: String) {
        viewModelScope.launch {
            _isTryingLogin.value = true

            val event = handleNetworkResult(
                result = authRepository.kakaoLogin(kakaoId),
                onSuccess = { IntroUiEvent.LoginSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.UNAUTHORIZED -> IntroUiEvent.NeedKakaoSignUp(kakaoId)
                        else -> IntroUiEvent.LoginFailure
                    }
                },
                onNetworkError = { IntroUiEvent.LoginFailure },
                onUnknownError = { IntroUiEvent.LoginFailure },
            )
            sendEvent(event)
            _isTryingLogin.value = false
        }
    }

    private fun googleLogin(googleId: String) {
        viewModelScope.launch {
            _isTryingLogin.value = true

            val event = handleNetworkResult(
                result = authRepository.googleLogin(googleId),
                onSuccess = { IntroUiEvent.LoginSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.UNAUTHORIZED -> IntroUiEvent.NeedGoogleSignUp(googleId)
                        else -> IntroUiEvent.LoginFailure
                    }
                },
                onNetworkError = { IntroUiEvent.LoginFailure },
                onUnknownError = { IntroUiEvent.LoginFailure },
            )
            sendEvent(event)
            _isTryingLogin.value = false
        }
    }

    private fun naverLogin(naverId: String) {
        viewModelScope.launch {
            _isTryingLogin.value = true

            val event = handleNetworkResult(
                result = authRepository.naverLogin(naverId),
                onSuccess = { IntroUiEvent.LoginSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.UNAUTHORIZED -> IntroUiEvent.NeedNaverSignUp(naverId)
                        else -> IntroUiEvent.LoginFailure
                    }
                },
                onNetworkError = { IntroUiEvent.LoginFailure },
                onUnknownError = { IntroUiEvent.LoginFailure },
            )
            sendEvent(event)
            _isTryingLogin.value = false
        }
    }

    private fun guestLogin(guestId: String) {
        viewModelScope.launch {
            _isTryingLogin.value = true

            val event = handleNetworkResult(
                result = authRepository.guestLogin(guestId),
                onSuccess = { IntroUiEvent.LoginSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.UNAUTHORIZED -> IntroUiEvent.NeedGuestSignUp(guestId)
                        else -> IntroUiEvent.LoginFailure
                    }
                },
                onNetworkError = { IntroUiEvent.LoginFailure },
                onUnknownError = { IntroUiEvent.LoginFailure },
            )
            sendEvent(event)
            _isTryingLogin.value = false
        }
    }
}
