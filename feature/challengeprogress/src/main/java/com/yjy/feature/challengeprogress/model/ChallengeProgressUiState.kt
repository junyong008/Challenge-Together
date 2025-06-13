package com.yjy.feature.challengeprogress.model

import com.yjy.model.challenge.RecoveryProgress

sealed interface ChallengeProgressUiState {
    data class Success(val recoveryProgress: RecoveryProgress) : ChallengeProgressUiState
    data object Loading : ChallengeProgressUiState
    data object Error : ChallengeProgressUiState
}

fun ChallengeProgressUiState.getOrNull(): RecoveryProgress? {
    return if (this is ChallengeProgressUiState.Success) {
        recoveryProgress
    } else {
        null
    }
}
