package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.database.model.TogetherChallengeEntity
import com.yjy.data.network.response.challenge.GetWaitingChallengeDetailResponse
import com.yjy.data.network.response.challenge.WaitingChallengeResponse
import com.yjy.model.challenge.DetailedWaitingChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.common.Tier

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

internal fun GetWaitingChallengeDetailResponse.toDetailedWaitingChallengeModel() = DetailedWaitingChallenge(
    id = challengeId,
    title = title,
    description = description,
    category = category.toCategory(),
    targetDays = targetDays.toTargetDays(),
    password = password,
    author = UserMapper.create(
        name = author.name,
        tier = Tier.getCurrentTier(author.bestRecordInSeconds),
    ),
    participants = participants.map {
        UserMapper.create(
            name = it.name,
            tier = Tier.getCurrentTier(it.bestRecordInSeconds),
        )
    },
    maxParticipantCounts = maxParticipantCount,
    isAuthor = isAuthor,
    isParticipated = isParticipated,
)

internal fun GetWaitingChallengeDetailResponse.toTogetherEntity() = TogetherChallengeEntity(
    id = challengeId,
    title = title,
    description = description,
    category = category,
    targetDays = targetDays,
    currentParticipantCount = participants.size,
    maxParticipantCount = maxParticipantCount,
    isPrivate = password.isNotEmpty(),
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
