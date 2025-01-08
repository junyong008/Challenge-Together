package com.yjy.feature.findpassword

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.findpassword.model.FindPasswordUiAction
import com.yjy.feature.findpassword.model.FindPasswordUiEvent
import com.yjy.feature.findpassword.model.FindPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<FindPasswordUiState> = MutableStateFlow(FindPasswordUiState())
    val uiState: StateFlow<FindPasswordUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<FindPasswordUiEvent>()
    val uiEvent: Flow<FindPasswordUiEvent> = _uiEvent.receiveAsFlow()

    private var timerJob: Job? = null

    private fun sendEvent(event: FindPasswordUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: FindPasswordUiAction) {
        when (action) {
            is FindPasswordUiAction.OnEmailUpdated -> updateEmail(action.email)
            is FindPasswordUiAction.OnSendVerifyCodeClick -> sendVerifyCode(action.email)
            is FindPasswordUiAction.OnVerifyCodeUpdated -> updateVerifyCode(action.code)
        }
    }

    private fun updateEmail(email: String) {
        if (email.length > MAX_EMAIL_LENGTH) return
        _uiState.update {
            val isValidEmailFormat = EMAIL_ADDRESS.matcher(email).matches()

            it.copy(
                email = email,
                isValidEmailFormat = isValidEmailFormat,
                canTrySendVerifyCode = isValidEmailFormat,
            )
        }
    }

    private fun sendVerifyCode(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingVerifyCode = true) }

            val event = handleNetworkResult(
                result = authRepository.requestVerifyCode(email),
                onSuccess = {
                    startTimer()
                    clearVerifyCode()
                    FindPasswordUiEvent.VerifyCodeSent
                },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.UNAUTHORIZED -> FindPasswordUiEvent.SendVerifyCodeFailure.UnregisteredEmail
                        HttpStatusCodes.BAD_REQUEST -> FindPasswordUiEvent.SendVerifyCodeFailure.InvalidEmail
                        else -> FindPasswordUiEvent.Failure.UnknownError
                    }
                },
                onNetworkError = { FindPasswordUiEvent.Failure.NetworkError },
                onUnknownError = { FindPasswordUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isSendingVerifyCode = false) }
        }
    }

    private fun updateVerifyCode(code: String) {
        if (code.length > VERIFY_CODE_LENGTH || code.any { !it.isDigit() }) return
        if (code.length == VERIFY_CODE_LENGTH) verifyCode(code)
        _uiState.update { it.copy(verifyCode = code) }
    }

    private fun clearVerifyCode() {
        _uiState.update { it.copy(verifyCode = "") }
    }

    private fun verifyCode(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifyingCode = true) }

            val event = handleNetworkResult(
                result = authRepository.verifyCode(uiState.value.email, code),
                onSuccess = { FindPasswordUiEvent.VerifySuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.BAD_REQUEST -> {
                            clearVerifyCode()
                            FindPasswordUiEvent.VerifyFailure.UnMatchedCode
                        }

                        HttpStatusCodes.GONE -> FindPasswordUiEvent.VerifyFailure.Timeout
                        else -> FindPasswordUiEvent.Failure.UnknownError
                    }
                },
                onNetworkError = { FindPasswordUiEvent.Failure.NetworkError },
                onUnknownError = { FindPasswordUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isVerifyingCode = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(minutes = TIMER_MIN, seconds = TIMER_SEC) }

        timerJob = viewModelScope.launch {
            while (uiState.value.minutes > 0 || uiState.value.seconds > 0) {
                delay(ONE_SECOND)
                _uiState.update {
                    it.copy(
                        minutes = if (it.seconds == 0) it.minutes - 1 else it.minutes,
                        seconds = if (it.seconds > 0) it.seconds - 1 else 59,
                    )
                }
            }
            sendEvent(FindPasswordUiEvent.VerifyFailure.Timeout)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    companion object {
        const val VERIFY_CODE_LENGTH = 6
        const val ONE_SECOND = 1000L
        const val TIMER_MIN = 5
        const val TIMER_SEC = 0
    }
}
