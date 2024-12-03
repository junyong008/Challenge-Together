package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.database.model.TogetherChallengeEntity
import com.yjy.data.network.response.ChallengeResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse
import com.yjy.data.network.response.WaitingChallengeResponse
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
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
    isPrivate = isPrivate,
    isCompleted = isCompleted,
    mode = if (isFreeMode) Mode.FREE.name else Mode.CHALLENGE.name,
)

internal fun ChallengeEntity.toSimpleStartedChallengeModel(): SimpleStartedChallenge {
    return SimpleStartedChallenge(
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
}

internal fun ChallengeEntity.toSimpleWaitingChallengeModel() = SimpleWaitingChallenge(
    id = id,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    isPrivate = isPrivate,
    currentParticipantCounts = currentParticipantCount,
    maxParticipantCounts = maxParticipantCount,
)

internal fun GetStartedChallengeDetailResponse.toDetailedStartedChallengeModel(): DetailedStartedChallenge {
    return DetailedStartedChallenge(
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
}

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

internal fun WaitingChallengeResponse.toTogetherEntity() = TogetherChallengeEntity(
    id = challengeId,
    title = title,
    description = description,
    category = category,
    targetDays = targetDays,
    currentParticipantCount = currentParticipantCount,
    maxParticipantCount = maxParticipantCount,
    isPrivate = isPrivate,
)

internal fun TogetherChallengeEntity.toModel() = SimpleWaitingChallenge(
    id = id,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    isPrivate = isPrivate,
    currentParticipantCounts = currentParticipantCount,
    maxParticipantCounts = maxParticipantCount,
)
