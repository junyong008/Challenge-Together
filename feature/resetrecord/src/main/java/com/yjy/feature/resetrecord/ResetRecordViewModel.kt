package com.yjy.feature.resetrecord

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.NetworkResult
import com.yjy.domain.GetResetRecordsUseCase
import com.yjy.feature.resetrecord.model.ResetRecordsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ResetRecordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getResetRecordsUseCase: GetResetRecordsUseCase,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<String?>("challengeId", null)

    val resetRecords = challengeId
        .filterNotNull()
        .flatMapLatest { challengeId ->
            getResetRecordsUseCase(challengeId)
        }.map { result ->
            when (result) {
                is NetworkResult.Success -> ResetRecordsUiState.Success(result.data)
                is NetworkResult.Failure -> ResetRecordsUiState.Error
            }
        }
        .restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ResetRecordsUiState.Loading,
        )

    fun retryOnError() {
        resetRecords.restart()
    }
}
