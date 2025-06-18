package com.yjy.feature.startedchallenge.model

data class StartedChallengeUiState(
    val isResetting: Boolean = false,
    val isContinuing: Boolean = false,
    val isDeleting: Boolean = false,
    val isEditingReasonToStart: Boolean = false,
)
