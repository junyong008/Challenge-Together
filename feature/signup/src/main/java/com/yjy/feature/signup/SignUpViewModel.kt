package com.yjy.feature.signup

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.yjy.core.common.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.core.common.constants.AuthConst.MAX_NICKNAME_LENGTH
import com.yjy.core.common.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.core.common.constants.AuthConst.MIN_NICKNAME_LENGTH
import com.yjy.core.common.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.core.common.network.HttpStatusCodes
import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
import com.yjy.core.navigation.Route
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
    saveStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arguments = saveStateHandle.toRoute<Route.SignUp.Nickname>()
    private val kakaoId = arguments.kakaoId
    private val googleId = arguments.googleId
    private val naverId = arguments.naverId

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<SignUpUiEvent>()
    val uiEvent: Flow<SignUpUiEvent> = _uiEvent.receiveAsFlow()

    fun processAction(action: SignUpUiAction) {
        when (action) {
            is SignUpUiAction.OnStartClick -> signUp(action.nickname, action.email, action.password)
            is SignUpUiAction.OnEmailPasswordContinueClick -> checkEmailDuplicate(action.email)
            is SignUpUiAction.OnEmailUpdated -> updateEmail(action.email)
            is SignUpUiAction.OnPasswordUpdated -> updatePassword(action.password)
            is SignUpUiAction.OnNicknameUpdated -> updateNickname(action.nickname)
        }
    }

    private fun signUp(nickname: String, email: String, password: String) {
        if (email.isBlank() && kakaoId.isBlank() && googleId.isBlank() && naverId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningUp = true) }

            val result = authRepository.signUp(
                nickname = nickname,
                email = email,
                password = password,
                kakaoId = kakaoId,
                googleId = googleId,
                naverId = naverId,
            )

            val event = when (result) {
                is NetworkResult.Success -> SignUpUiEvent.SignUpSuccess

                is NetworkResult.Failure.NetworkError ->
                    SignUpUiEvent.SignUpFailure.NetworkError

                is NetworkResult.Failure.HttpError -> when (result.code) {
                    HttpStatusCodes.CONFLICT -> SignUpUiEvent.SignUpFailure.DuplicatedNickname
                    else -> SignUpUiEvent.SignUpFailure.UnknownError
                }

                else -> SignUpUiEvent.SignUpFailure.UnknownError
            }
            sendEvent(event)

            _uiState.update { it.copy(isSigningUp = false) }
        }
    }

    private fun checkEmailDuplicate(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isValidatingEmail = true) }

            val event = when (val result = authRepository.checkEmailDuplicate(email)) {
                is NetworkResult.Success -> SignUpUiEvent.EmailPasswordVerified

                is NetworkResult.Failure.NetworkError ->
                    SignUpUiEvent.EmailPasswordVerifyFailure.NetworkError

                is NetworkResult.Failure.HttpError -> when (result.code) {
                    HttpStatusCodes.CONFLICT -> SignUpUiEvent.EmailPasswordVerifyFailure.DuplicatedEmail
                    else -> SignUpUiEvent.EmailPasswordVerifyFailure.UnknownError
                }

                else -> SignUpUiEvent.EmailPasswordVerifyFailure.UnknownError
            }
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
        val pattern = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]*$"
        return this.matches(pattern.toRegex()).not()
    }

    private fun String.hasConsonantOrVowel(): Boolean {
        val consonantOrVowelPattern = ".*[ㄱ-ㅎㅏ-ㅣ].*"
        return this.matches(consonantOrVowelPattern.toRegex())
    }

    private fun sendEvent(event: SignUpUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}
