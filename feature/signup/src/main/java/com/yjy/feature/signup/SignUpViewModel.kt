package com.yjy.feature.signup

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.common.core.constants.AuthConst.MAX_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.auth.api.AuthRepository
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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignUpUiState> = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SignUpUiEvent>()
    val uiEvent: Flow<SignUpUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: SignUpUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: SignUpUiAction) {
        when (action) {
            is SignUpUiAction.OnStartClick -> signUp(
                nickname = action.nickname,
                email = action.email,
                password = action.password,
                kakaoId = action.kakaoId,
                googleId = action.googleId,
                naverId = action.naverId,
                guestId = action.guestId,
            )

            is SignUpUiAction.OnEmailPasswordContinueClick -> checkEmailDuplicate(action.email)
            is SignUpUiAction.OnEmailUpdated -> updateEmail(action.email)
            is SignUpUiAction.OnPasswordUpdated -> updatePassword(action.password)
            is SignUpUiAction.OnNicknameUpdated -> updateNickname(action.nickname)
        }
    }

    private fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
        guestId: String,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningUp = true) }

            val event = handleNetworkResult(
                result = authRepository.signUp(nickname, email, password, kakaoId, googleId, naverId, guestId),
                onSuccess = { SignUpUiEvent.SignUpSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.CONFLICT -> SignUpUiEvent.DuplicatedNickname
                        else -> SignUpUiEvent.Failure.UnknownError
                    }
                },
                onNetworkError = { SignUpUiEvent.Failure.NetworkError },
                onUnknownError = { SignUpUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isSigningUp = false) }
        }
    }

    private fun checkEmailDuplicate(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isValidatingEmail = true) }

            val event = handleNetworkResult(
                result = authRepository.checkEmailDuplicate(email),
                onSuccess = { SignUpUiEvent.EmailPasswordVerified },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.CONFLICT -> SignUpUiEvent.DuplicatedEmail
                        else -> SignUpUiEvent.Failure.UnknownError
                    }
                },
                onNetworkError = { SignUpUiEvent.Failure.NetworkError },
                onUnknownError = { SignUpUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isValidatingEmail = false) }
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
        _uiState.update {
            val isPasswordLongEnough = password.length >= MIN_PASSWORD_LENGTH
            val isPasswordContainNumber = password.any { it.isDigit() }

            it.copy(
                password = password.filter { !it.isWhitespace() },
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
    ): Boolean {
        return email.isNotEmpty() &&
            password.isNotEmpty() &&
            isValidEmailFormat &&
            isPasswordLongEnough &&
            isPasswordContainNumber
    }

    private fun updateNickname(nickname: String) {
        if (nickname.hasInValidCharacters() || nickname.length > MAX_NICKNAME_LENGTH) return
        _uiState.update {
            val isNicknameLengthValid = nickname.length >= MIN_NICKNAME_LENGTH
            val isNicknameHasOnlyConsonantOrVowel = nickname.hasConsonantOrVowel()

            it.copy(
                nickname = nickname,
                isNicknameLengthValid = isNicknameLengthValid,
                isNicknameHasOnlyConsonantOrVowel = isNicknameHasOnlyConsonantOrVowel,
                canTryStart = isNicknameLengthValid && !isNicknameHasOnlyConsonantOrVowel,
            )
        }
    }

    private fun String.hasInValidCharacters(): Boolean {
        val specialCharPattern = ".*[\\p{P}\\p{S}\\p{C}].*"
        return this.matches(specialCharPattern.toRegex())
    }

    private fun String.hasConsonantOrVowel(): Boolean {
        val consonantOrVowelPattern = ".*[ㄱ-ㅎㅏ-ㅣ].*"
        return this.matches(consonantOrVowelPattern.toRegex())
    }
}
