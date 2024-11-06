package com.yjy.model.challenge.base

import java.time.LocalDateTime

interface TimerChallenge {
    val currentRecordInSeconds: Long
    val recentResetDateTime: LocalDateTime
}
