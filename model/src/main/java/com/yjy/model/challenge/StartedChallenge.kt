package com.yjy.model.challenge

import com.yjy.model.challenge.base.Challenge
import com.yjy.model.challenge.base.TimerChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.ParticipantInfo
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime

data class StartedChallenge(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: Category,
    override val targetDays: TargetDays,
    override val participantInfo: ParticipantInfo?,
    override val currentRecordInSeconds: Long?,
    override val recentResetDateTime: LocalDateTime,
    val mode: Mode,
    val isCompleted: Boolean,
    val startDateTime: LocalDateTime?,
) : Challenge(), TimerChallenge
