package com.yjy.feature.changename

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.AuthConst.MAX_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_NICKNAME_LENGTH
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.changename.model.ChangeNameUiAction
import com.yjy.feature.changename.model.ChangeNameUiEvent
import com.yjy.feature.changename.model.ChangeNameUiState
import com.yjy.feature.changename.model.RemainSecondForChangeUiState
import com.yjy.feature.changename.model.UserNameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeNameViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val remoteUserName = flow {
        userRepository.getUserName()
            .onSuccess { emit(UserNameUiState.Success(it)) }
            .onFailure { emit(UserNameUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserNameUiState.Loading,
    )

    val remainSecondsForChange = flow {
        userRepository.getRemainSecondsForChangeName()
            .onSuccess { emit(RemainSecondForChangeUiState.Success(it)) }
            .onFailure { emit(RemainSecondForChangeUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RemainSecondForChangeUiState.Loading,
    )

    private val _uiState: MutableStateFlow<ChangeNameUiState> = MutableStateFlow(ChangeNameUiState())
    val uiState: StateFlow<ChangeNameUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<ChangeNameUiEvent>()
    val uiEvent: Flow<ChangeNameUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: ChangeNameUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    fun processAction(action: ChangeNameUiAction) {
        when (action) {
            ChangeNameUiAction.OnRetryClick -> retryOnError()
            is ChangeNameUiAction.OnChangeClick -> changeName(action.name)
            is ChangeNameUiAction.OnNameUpdated -> updateName(action.name)
        }
    }

    private fun retryOnError() {
        remoteUserName.restart()
        remainSecondsForChange.restart()
    }

    private fun changeName(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingName = true) }

            val event = handleNetworkResult(
                result = userRepository.changeUserName(name),
                onSuccess = { ChangeNameUiEvent.ChangeSuccess },
                onHttpError = { code ->
                    when (code) {
                        HttpStatusCodes.CONFLICT -> ChangeNameUiEvent.Failure.DuplicatedName
                        else -> ChangeNameUiEvent.Failure.UnknownError
                    }
                },
                onNetworkError = { ChangeNameUiEvent.Failure.NetworkError },
                onUnknownError = { ChangeNameUiEvent.Failure.UnknownError },
            )
            sendEvent(event)
            _uiState.update { it.copy(isChangingName = false) }
        }
    }

    private fun updateName(name: String) {
        if (name.hasInValidCharacters() || name.length > MAX_NICKNAME_LENGTH) return
        _uiState.update {
            val isNameLengthValid = name.length >= MIN_NICKNAME_LENGTH
            val isNameHasOnlyConsonantOrVowel = name.hasConsonantOrVowel()

            it.copy(
                name = name,
                isNameLengthValid = isNameLengthValid,
                isNameHasOnlyConsonantOrVowel = isNameHasOnlyConsonantOrVowel,
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
