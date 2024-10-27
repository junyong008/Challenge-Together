package com.yjy.model.challenge

import java.time.LocalDateTime

data class StartedChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    override val currentParticipantCount: Int? = null,
    override val maxParticipantCount: Int? = null,
    val mode: Mode,
    val recentResetDateTime: LocalDateTime,
    val currentRecordInSeconds: Long? = null,
    val startDateTime: LocalDateTime? = null,
) : Challenge(
    id = id,
    title = title,
    description = description,
    category = category,
    targetDays = targetDays,
    currentParticipantCount = currentParticipantCount,
    maxParticipantCount = maxParticipantCount,
)
