package com.yjy.feature.waitingchallenge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.HttpStatusCodes.CONFLICT
import com.yjy.common.network.HttpStatusCodes.FORBIDDEN
import com.yjy.common.network.HttpStatusCodes.NOT_FOUND
import com.yjy.common.network.HttpStatusCodes.UNAUTHORIZED
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.handleNetworkResult
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.WaitingChallengeRepository
import com.yjy.feature.waitingchallenge.model.ChallengeDetailUiState
import com.yjy.feature.waitingchallenge.model.WaitingChallengeUiAction
import com.yjy.feature.waitingchallenge.model.WaitingChallengeUiEvent
import com.yjy.feature.waitingchallenge.model.WaitingChallengeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingChallengeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val waitingChallengeRepository: WaitingChallengeRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)
    private val password = MutableStateFlow("")

    private val _uiState = MutableStateFlow(WaitingChallengeUiState())
    val uiState: StateFlow<WaitingChallengeUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<WaitingChallengeUiEvent>()
    val uiEvent: Flow<WaitingChallengeUiEvent> = _uiEvent.receiveAsFlow()

    val challengeDetail = combine(
        challengeId.filterNotNull(),
        password,
    ) { challengeId, password ->
        Pair(challengeId, password)
    }.flatMapLatest { (challengeId, password) ->
        dismissPasswordDialog()
        val state = handleNetworkResult(
            result = waitingChallengeRepository.getWaitingChallengeDetail(challengeId, password),
            onSuccess = { challenge ->
                updatePassword(challenge.password)
                ChallengeDetailUiState.Success(challenge)
            },
            onHttpError = { code ->
                if (code == FORBIDDEN) {
                    showPasswordDialog()
                    return@handleNetworkResult ChallengeDetailUiState.Loading
                }

                when (code) {
                    NOT_FOUND -> WaitingChallengeUiEvent.LoadFailure.NotFound
                    CONFLICT -> WaitingChallengeUiEvent.LoadFailure.AlreadyStarted
                    UNAUTHORIZED -> WaitingChallengeUiEvent.LoadFailure.PasswordIncorrect
                    else -> WaitingChallengeUiEvent.LoadFailure.Unknown
                }.also { sendEvent(it) }

                ChallengeDetailUiState.Loading
            },
            onNetworkError = {
                sendEvent(WaitingChallengeUiEvent.LoadFailure.Network)
                ChallengeDetailUiState.Loading
            },
            onUnknownError = {
                sendEvent(WaitingChallengeUiEvent.LoadFailure.Unknown)
                ChallengeDetailUiState.Loading
            },
        )
        flowOf(state)
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ChallengeDetailUiState.Loading,
    )

    private fun sendEvent(event: WaitingChallengeUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: WaitingChallengeUiAction) {
        when (action) {
            is WaitingChallengeUiAction.OnEnterPassword -> updatePassword(action.password)
            WaitingChallengeUiAction.OnDismissPasswordDialog -> cancelPasswordInput()
            WaitingChallengeUiAction.OnPasswordCopy -> sendEvent(WaitingChallengeUiEvent.PasswordCopied)
            WaitingChallengeUiAction.OnRefreshClick -> challengeDetail.restart()
            is WaitingChallengeUiAction.OnDeleteClick -> deleteChallenge(action.challengeId)
            is WaitingChallengeUiAction.OnStartClick -> startChallenge(action.challengeId)
            is WaitingChallengeUiAction.OnJoinClick -> joinChallenge(action.challengeId)
            is WaitingChallengeUiAction.OnLeaveClick -> leaveChallenge(action.challengeId)
        }
    }

    private fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    private fun showPasswordDialog() {
        _uiState.update { it.copy(shouldShowPasswordDialog = true) }
    }

    private fun dismissPasswordDialog() {
        _uiState.update { it.copy(shouldShowPasswordDialog = false) }
    }

    private fun cancelPasswordInput() {
        dismissPasswordDialog()
        sendEvent(WaitingChallengeUiEvent.PasswordInputCanceled)
    }

    private fun deleteChallenge(challengeId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isDeleting = true) }

        waitingChallengeRepository.deleteWaitingChallenge(challengeId)
            .onSuccess { sendEvent(WaitingChallengeUiEvent.DeleteSuccess) }
            .onFailure { sendEvent(WaitingChallengeUiEvent.DeleteFailure) }
        _uiState.update { it.copy(isDeleting = false) }
    }

    private fun startChallenge(challengeId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isActionProcessing = true) }

        waitingChallengeRepository.startWaitingChallenge(challengeId)
            .onSuccess { sendEvent(WaitingChallengeUiEvent.StartSuccess(challengeId)) }
            .onFailure { sendEvent(WaitingChallengeUiEvent.StartFailure) }
        _uiState.update { it.copy(isActionProcessing = false) }
    }

    private fun joinChallenge(challengeId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isActionProcessing = true) }

        waitingChallengeRepository.joinWaitingChallenge(challengeId)
            .onFailure {
                if (it is NetworkResult.Failure.HttpError && it.code == FORBIDDEN) {
                    sendEvent(WaitingChallengeUiEvent.JoinFailure.Full)
                } else {
                    sendEvent(WaitingChallengeUiEvent.JoinFailure.Unknown)
                }
            }
        challengeDetail.restart()
        _uiState.update { it.copy(isActionProcessing = false) }
    }

    private fun leaveChallenge(challengeId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isActionProcessing = true) }

        waitingChallengeRepository.leaveWaitingChallenge(challengeId)
            .onFailure { sendEvent(WaitingChallengeUiEvent.LeaveFailure.Unknown) }
        challengeDetail.restart()
        _uiState.update { it.copy(isActionProcessing = false) }
    }
}
