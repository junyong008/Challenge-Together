package com.yjy.data.challenge.impl.mapper

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.response.ChallengeResponse
import com.yjy.model.challenge.ChallengeFactory
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

internal fun ChallengeResponse.toEntity() = ChallengeEntity(
    id = challengeId,
    title = title,
    description = description,
    category = category,
    targetDays = targetDays,
    currentParticipantCount = currentParticipantCount,
    maxParticipantCount = maxParticipantCount,
    recentResetDateTime = recentResetDateTime.toLocalDateTime(),
    isStarted = isStarted,
    isPrivate = password.isNotEmpty(),
    isCompleted = isCompleted,
    mode = if (isFreeMode) Mode.FREE.name else Mode.CHALLENGE.name,
)

internal fun ChallengeEntity.toStartedChallengeModel(currentTime: LocalDateTime): StartedChallenge {
    requireNotNull(recentResetDateTime) { "Started challenge must have reset time" }

    val targetDays = targetDays.toTargetDays()
    val currentRecordInSeconds = calculateCurrentRecord(recentResetDateTime!!, currentTime, targetDays)

    return ChallengeFactory.createStartedChallenge(
        id = id,
        title = title,
        description = description,
        category = category.toCategory(),
        targetDays = targetDays,
        mode = Mode.valueOf(mode),
        recentResetDateTime = recentResetDateTime!!,
        isCompleted = isCompleted,
        currentRecordInSeconds = currentRecordInSeconds,
    )
}

private fun calculateCurrentRecord(
    recentResetDateTime: LocalDateTime,
    currentTime: LocalDateTime,
    targetDays: TargetDays,
): Long {
    val currentRecord = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime)
    return when (targetDays) {
        is TargetDays.Fixed -> currentRecord.coerceAtMost(targetDays.days * SECONDS_PER_DAY)
        TargetDays.Infinite -> currentRecord
    }
}

internal fun ChallengeEntity.toWaitingChallengeModel() = ChallengeFactory.createWaitingChallenge(
    id = id,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    currentCount = currentParticipantCount,
    maxCount = maxParticipantCount,
    isPrivate = isPrivate,
)
