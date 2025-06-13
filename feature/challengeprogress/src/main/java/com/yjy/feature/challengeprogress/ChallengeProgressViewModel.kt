package com.yjy.feature.challengeprogress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.feature.challengeprogress.model.ChallengeProgressUiState
import com.yjy.model.challenge.RecoveryProgress
import com.yjy.model.challenge.core.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChallengeProgressViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    challengePreferencesRepository: ChallengePreferencesRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    val progressState = challengeId
        .filterNotNull()
        .flatMapLatest { id ->
            // TODO: 서버에서 데이터 받기.
            flow<NetworkResult<RecoveryProgress>> {
                delay(300)
                emit(
                    NetworkResult.Success(
                        RecoveryProgress(
                            challengeId = id,
                            category = Category.QUIT_SMOKING,
                            score = 1536,
                        )
                    )
                )
            }
        }.map { result ->
            when (result) {
                is NetworkResult.Failure -> ChallengeProgressUiState.Error
                is NetworkResult.Success -> ChallengeProgressUiState.Success(result.data)
            }
        }.restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChallengeProgressUiState.Loading,
        )

    fun retryOnError() {
        progressState.restart()
    }
}
