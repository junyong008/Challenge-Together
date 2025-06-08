package com.yjy.feature.resetrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.domain.GetResetInfoUseCase
import com.yjy.feature.resetrecord.model.ResetInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class ResetRecordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getResetInfoUseCase: GetResetInfoUseCase,
    challengePreferencesRepository: ChallengePreferencesRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    val resetInfo = merge(
        challengeId,
        challengePreferencesRepository.timeChangedFlow.map { challengeId.value },
    ).filterNotNull().flatMapLatest { id ->
        getResetInfoUseCase(id)
    }.map { result ->
        when (result) {
            is NetworkResult.Failure -> ResetInfoUiState.Error
            is NetworkResult.Success -> ResetInfoUiState.Success(result.data)
        }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResetInfoUiState.Loading,
    )

    fun retryOnError() {
        resetInfo.restart()
    }
}
