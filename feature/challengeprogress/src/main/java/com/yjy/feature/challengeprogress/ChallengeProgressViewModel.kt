package com.yjy.feature.challengeprogress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.feature.challengeprogress.model.ChallengeProgressUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ChallengeProgressViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    startedChallengeRepository: StartedChallengeRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    val progressState = challengeId
        .filterNotNull()
        .flatMapLatest { id ->
            flow {
                startedChallengeRepository.getChallengeProgress(id)
                    .onSuccess { emit(ChallengeProgressUiState.Success(it)) }
                    .onFailure { emit(ChallengeProgressUiState.Error) }
            }
        }.restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = ChallengeProgressUiState.Loading,
        )

    fun retryOnError() {
        progressState.restart()
    }
}
