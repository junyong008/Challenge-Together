package com.yjy.feature.signup

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.yjy.common.core.base.BaseViewModel
import com.yjy.common.core.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.common.core.constants.AuthConst.MAX_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.common.navigation.AuthRoute
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.signup.model.SignUpUiAction
import com.yjy.feature.signup.model.SignUpUiEvent
import com.yjy.feature.signup.model.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    saveStateHandle: SavedStateHandle,
) : BaseViewModel<SignUpUiState, SignUpUiEvent>(initialState = SignUpUiState()) {

    private val arguments = saveStateHandle.toRoute<AuthRoute.SignUp.Nickname>()
    private val kakaoId = arguments.kakaoId
    private val googleId = arguments.googleId
    private val naverId = arguments.naverId

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
            updateState { copy(isSigningUp = true) }

            val event = handleNetworkResult(
                result = authRepository.signUp(nickname, email, password, kakaoId, googleId, naverId),
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
            updateState { copy(isSigningUp = false) }
        }
    }

    private fun checkEmailDuplicate(email: String) {
        viewModelScope.launch {
            updateState { copy(isValidatingEmail = true) }

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
            updateState { copy(isValidatingEmail = false) }
        }
    }

    private fun updateEmail(email: String) {
        if (email.length > MAX_EMAIL_LENGTH) return
        updateState {
            val isValidEmailFormat = EMAIL_ADDRESS.matcher(email).matches()

            copy(
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
        updateState {
            val isPasswordLongEnough = password.length >= MIN_PASSWORD_LENGTH
            val isPasswordContainNumber = password.any { it.isDigit() }

            copy(
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
        updateState {
            val isNicknameLengthValid = nickname.length >= MIN_NICKNAME_LENGTH
            val isNicknameHasOnlyConsonantOrVowel = nickname.hasConsonantOrVowel()

            copy(
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
}
