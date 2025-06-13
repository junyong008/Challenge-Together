package com.yjy.model.challenge

import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.User
import java.time.LocalDateTime

data class ChallengeRank(
    val rank: Int,
    val scoreRank: Int,
    val user: User,
    val memberId: Int,
    val recentResetDateTime: LocalDateTime,
    val recoveryScore: Int,
    val currentRecordInSeconds: Long,
    val targetDays: TargetDays,
    val isInActive: Boolean,
    val isComplete: Boolean,
    val isMine: Boolean,
)
