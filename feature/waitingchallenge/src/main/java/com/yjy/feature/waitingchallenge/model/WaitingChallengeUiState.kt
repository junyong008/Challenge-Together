package com.yjy.feature.waitingchallenge.model

data class WaitingChallengeUiState(
    val shouldShowPasswordDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val isActionProcessing: Boolean = false,
)
