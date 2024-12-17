package com.yjy.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.GetStartedChallengesUseCase
import com.yjy.domain.LogoutUseCase
import com.yjy.feature.my.model.MyUiAction
import com.yjy.feature.my.model.MyUiEvent
import com.yjy.feature.my.model.RecordUiState
import com.yjy.feature.my.model.UserNameUiState
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.Mode
import com.yjy.model.common.Tier
import com.yjy.model.common.TierProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val logoutUseCase: LogoutUseCase,
    getStartedChallengesUseCase: GetStartedChallengesUseCase,
    challengePreferencesRepository: ChallengePreferencesRepository,
) : ViewModel() {

    val currentTier = challengePreferencesRepository.localTier
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Tier.UNSPECIFIED,
        )

    val tierProgress = getStartedChallengesUseCase()
        .map { challenges ->
            val currentBestRecord = challenges
                .filter { it.mode == Mode.CHALLENGE }
                .getBestRecord()

            Tier.getTierProgress(currentTier.value, currentBestRecord)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TierProgress(0, 0f),
        )

    val userName = flow {
        userRepository.getUserName()
            .onSuccess { emit(UserNameUiState.Success(it)) }
            .onFailure { emit(UserNameUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserNameUiState.Loading,
    )

    val records = flow {
        challengePreferencesRepository.getRecords()
            .onSuccess { emit(RecordUiState.Success(it)) }
            .onFailure { emit(RecordUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RecordUiState.Loading,
    )

    private val _uiEvent = Channel<MyUiEvent>()
    val uiEvent: Flow<MyUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: MyUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    private fun List<SimpleStartedChallenge>.getBestRecord(): Long {
        return this.maxOfOrNull { it.currentRecordInSeconds } ?: 0
    }

    fun processAction(action: MyUiAction) {
        when (action) {
            MyUiAction.OnRetryClick -> retryOnError()
            MyUiAction.OnLogoutClick -> logout()
            MyUiAction.OnFindEmailAppFailure -> sendUiEvent(MyUiEvent.FindEmailAppFailure)
            MyUiAction.OnSendEmailFailure -> sendUiEvent(MyUiEvent.SendEmailFailure)
        }
    }

    private fun retryOnError() {
        userName.restart()
        records.restart()
    }

    private fun logout() {
        viewModelScope.launch {
            sendUiEvent(MyUiEvent.Logout)
            logoutUseCase()
        }
    }
}
