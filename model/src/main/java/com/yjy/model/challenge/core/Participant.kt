package com.yjy.model.challenge.core

import com.yjy.model.common.User
import java.time.LocalDateTime

data class Participant(
    val user: User,
    val recentResetDateTime: LocalDateTime,
)
