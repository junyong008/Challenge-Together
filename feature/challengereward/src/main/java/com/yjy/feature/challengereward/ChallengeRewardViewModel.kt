package com.yjy.feature.challengereward

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.feature.challengereward.model.ChallengeRewardUiState
import com.yjy.feature.challengereward.model.RewardInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ChallengeRewardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    startedChallengeRepository: StartedChallengeRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    private val _uiState = MutableStateFlow(ChallengeRewardUiState())
    val uiState: StateFlow<ChallengeRewardUiState> = _uiState.asStateFlow()

    val rewardInfoState = challengeId
        .filterNotNull()
        .flatMapLatest { id ->
            flow {
                emit(RewardInfoUiState.Success(null))
            }
        }.restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = RewardInfoUiState.Loading,
        )

    fun retryOnError() {
        rewardInfoState.restart()
    }
}
