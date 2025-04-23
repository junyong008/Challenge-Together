package com.yjy.feature.startedchallenge.model

import java.time.LocalDateTime

sealed interface StartedChallengeUiAction {
    data class OnResetClick(
        val challengeId: Int,
        val resetDateTime: LocalDateTime,
        val memo: String,
    ) : StartedChallengeUiAction

    data class OnDeleteChallengeClick(val challengeId: Int) : StartedChallengeUiAction
}
