package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.model.challenge.Mode
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.WaitingChallenge

internal fun GetMyChallengesResponse.toEntity() = ChallengeEntity(
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

internal fun ChallengeEntity.toStartedChallengeModel(timeDiff: Long) = StartedChallenge(
    id = id,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    recentResetDateTime = recentResetDateTime?.addSeconds(timeDiff)!!,
    mode = Mode.valueOf(mode),
)

internal fun ChallengeEntity.toWaitingChallengeModel() = WaitingChallenge(
    id = id,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    currentParticipantCount = currentParticipantCount,
    maxParticipantCount = maxParticipantCount,
    isPrivate = isPrivate,
)