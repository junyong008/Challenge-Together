package com.yjy.model.challenge

import com.yjy.model.challenge.base.Challenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime

data class DetailedStartedChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    val currentRecordInSeconds: Long,
    val recentResetDateTime: LocalDateTime,
    val mode: Mode,
    val isCompleted: Boolean,
    val rank: Int,
    val startDateTime: LocalDateTime,
    val currentParticipantCounts: Int,
) : Challenge()
