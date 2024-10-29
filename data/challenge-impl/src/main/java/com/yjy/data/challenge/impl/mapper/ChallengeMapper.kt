package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.response.ChallengeResponse
import com.yjy.model.challenge.ChallengeFactory
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.core.Mode

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

internal fun ChallengeEntity.toStartedChallengeModel(timeDiff: Long): StartedChallenge {
    val resetDateTime = recentResetDateTime?.addSeconds(timeDiff)
        ?: throw IllegalStateException("Started challenge must have reset time")

    return ChallengeFactory.createStartedChallenge(
        id = id,
        title = title,
        description = description,
        category = category.toCategory(),
        targetDays = targetDays.toTargetDays(),
        mode = Mode.valueOf(mode),
        recentResetDateTime = resetDateTime,
        isCompleted = isCompleted,
    )
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
