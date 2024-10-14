package com.yjy.model.challenge

import java.time.LocalDateTime

data class StartedChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val targetDays: TargetDays,
    override val participants: List<Participant>,
    override val category: Category,
    val mode: Mode,
    val startTime: LocalDateTime,
    val recentResetTime: LocalDateTime,
) : Challenge(id, title, description, targetDays, participants, category)
