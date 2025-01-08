package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.response.challenge.GetStartedChallengeDetailResponse
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.Mode

internal fun ChallengeEntity.toSimpleStartedChallengeModel() = SimpleStartedChallenge(
    id = id,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    currentRecordInSeconds = 0L,
    recentResetDateTime = recentResetDateTime,
    mode = Mode.valueOf(mode),
    isCompleted = isCompleted,
)

internal fun GetStartedChallengeDetailResponse.toDetailedStartedChallengeModel() = DetailedStartedChallenge(
    id = challengeId,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    currentRecordInSeconds = 0L,
    recentResetDateTime = recentResetDateTime.toLocalDateTime(),
    mode = if (isFreeMode) Mode.FREE else Mode.CHALLENGE,
    isCompleted = isCompleted,
    rank = rank,
    startDateTime = startDateTime.toLocalDateTime(),
    currentParticipantCounts = currentParticipantCount,
)

internal fun GetStartedChallengeDetailResponse.toEntity() = ChallengeEntity(
    id = challengeId,
    title = title,
    description = description,
    category = category,
    targetDays = targetDays,
    currentParticipantCount = currentParticipantCount,
    maxParticipantCount = maxParticipantCount,
    recentResetDateTime = recentResetDateTime.toLocalDateTime(),
    isStarted = true,
    isPrivate = isPrivate,
    isCompleted = isCompleted,
    mode = if (isFreeMode) Mode.FREE.name else Mode.CHALLENGE.name,
)
