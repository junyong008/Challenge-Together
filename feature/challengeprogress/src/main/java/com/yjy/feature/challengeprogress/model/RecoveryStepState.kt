package com.yjy.feature.challengeprogress.model

enum class RecoveryStepState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    ;

    companion object {
        fun fromScore(currentScore: Int, requireScore: Int): RecoveryStepState {
            return when {
                currentScore >= requireScore -> COMPLETED
                currentScore > 0 -> IN_PROGRESS
                else -> NOT_STARTED
            }
        }
    }
}
