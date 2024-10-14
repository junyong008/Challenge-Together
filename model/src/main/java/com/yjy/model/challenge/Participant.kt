package com.yjy.model.challenge

import com.yjy.model.User
import java.time.LocalDateTime

data class Participant(
    val user: User,
    val lastResetDate: LocalDateTime,
)
