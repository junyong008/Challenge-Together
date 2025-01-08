package com.yjy.model.common

import java.time.LocalDateTime

data class Ban(
    val reason: ReportReason,
    val endAt: LocalDateTime,
)
