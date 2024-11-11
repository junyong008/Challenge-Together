package com.yjy.model.challenge

import java.time.LocalDateTime

data class ResetRecord(
    val resetDateTime: LocalDateTime,
    val recordInSeconds: Long,
    val content: String,
)
