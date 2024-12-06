package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.response.challenge.ChallengeResponse
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
