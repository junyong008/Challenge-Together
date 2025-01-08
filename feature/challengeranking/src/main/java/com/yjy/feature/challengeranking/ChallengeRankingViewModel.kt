package com.yjy.feature.challengeranking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.fold
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.domain.GetChallengeRanksUseCase
import com.yjy.feature.challengeranking.model.ChallengeRankingUiAction
import com.yjy.feature.challengeranking.model.ChallengeRankingUiEvent
import com.yjy.feature.challengeranking.model.ChallengeRankingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeRankingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getChallengeRanksUseCase: GetChallengeRanksUseCase,
    challengePreferencesRepository: ChallengePreferencesRepository,
    private val startedChallengeRepository: StartedChallengeRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    private val _uiEvent = Channel<ChallengeRankingUiEvent>()
    val uiEvent: Flow<ChallengeRankingUiEvent> = _uiEvent.receiveAsFlow()

    val challengeRanks = merge(
        challengeId,
        challengePreferencesRepository.timeChangedFlow.map { challengeId.value },
    ).filterNotNull().flatMapLatest { id ->
        getChallengeRanksUseCase(id)
    }.map { result ->
        result.fold(
            onSuccess = { ChallengeRankingUiState.Success(it) },
            onFailure = { ChallengeRankingUiState.Error },
        )
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChallengeRankingUiState.Loading,
    )

    private fun sendEvent(event: ChallengeRankingUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: ChallengeRankingUiAction) {
        when (action) {
            ChallengeRankingUiAction.RetryOnError -> retryOnError()
            is ChallengeRankingUiAction.OnForceRemoveClick -> forceRemove(action.memberId)
        }
    }

    private fun retryOnError() {
        challengeRanks.restart()
    }

    private fun forceRemove(memberId: Int) = viewModelScope.launch {
        val event = handleNetworkResult(
            result = startedChallengeRepository.forceRemoveStartedChallengeMember(memberId),
            onSuccess = {
                challengeRanks.restart()
                ChallengeRankingUiEvent.ForceRemoveSuccess
            },
            onHttpError = { code ->
                when (code) {
                    HttpStatusCodes.BAD_REQUEST -> {
                        challengeRanks.restart()
                        ChallengeRankingUiEvent.ForceRemoveFailed.UserReConnected
                    }

                    else -> ChallengeRankingUiEvent.ForceRemoveFailed.UnknownError
                }
            },
            onNetworkError = { ChallengeRankingUiEvent.ForceRemoveFailed.NetworkError },
            onUnknownError = { ChallengeRankingUiEvent.ForceRemoveFailed.UnknownError },
        )
        sendEvent(event)
    }
}
