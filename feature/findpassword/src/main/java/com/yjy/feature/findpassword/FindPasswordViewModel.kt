package com.yjy.feature.findpassword

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.viewModelScope
import com.yjy.core.common.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.core.common.network.HttpStatusCodes
import com.yjy.core.common.network.handleNetworkResult
import com.yjy.core.common.ui.BaseViewModel
import com.yjy.core.data.repository.AuthRepository
import com.yjy.feature.findpassword.model.FindPasswordUiAction
import com.yjy.feature.findpassword.model.FindPasswordUiEvent
import com.yjy.feature.findpassword.model.FindPasswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel<FindPasswordUiState, FindPasswordUiEvent>(initialState = FindPasswordUiState()) {

    private var timerJob: Job? = null

    fun processAction(action: FindPasswordUiAction) {
        when (action) {
            is FindPasswordUiAction.OnEmailUpdated -> updateEmail(action.email)
            is FindPasswordUiAction.OnSendVerifyCodeClick -> sendVerifyCode(action.email)
            is FindPasswordUiAction.OnVerifyCodeUpdated -> updateVerifyCode(action.code)
        }
    }

    private fun updateEmail(email: String) {
        if (email.length > MAX_EMAIL_LENGTH) return
        updateState {
            val isValidEmailFormat = EMAIL_ADDRESS.matcher(email).matches()

            copy(
                email = email,
                isValidEmailFormat = isValidEmailFormat,
                canTrySendVerifyCode = isValidEmailFormat,
            )
        }
    }

    private fun sendVerifyCode(email: String) {
        viewModelScope.launch {
            updateState { copy(isSendingVerifyCode = true) }

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
            updateState { copy(isSendingVerifyCode = false) }
        }
    }

    private fun updateVerifyCode(code: String) {
        if (code.length > VERIFY_CODE_LENGTH || code.any { !it.isDigit() }) return
        if (code.length == VERIFY_CODE_LENGTH) verifyCode(code)
        updateState { copy(verifyCode = code) }
    }

    private fun clearVerifyCode() {
        updateState { copy(verifyCode = "") }
    }

    private fun verifyCode(code: String) {
        viewModelScope.launch {
            updateState { copy(isVerifyingCode = true) }

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
            updateState { copy(isVerifyingCode = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        updateState { copy(minutes = TIMER_MIN, seconds = TIMER_SEC) }

        timerJob = viewModelScope.launch {
            while (uiState.value.minutes > 0 || uiState.value.seconds > 0) {
                delay(1000L)
                updateState {
                    copy(
                        minutes = if (seconds == 0) minutes - 1 else minutes,
                        seconds = if (seconds > 0) seconds - 1 else 59,
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
        const val TIMER_MIN = 5
        const val TIMER_SEC = 0
    }
}
