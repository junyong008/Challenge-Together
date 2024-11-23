package com.yjy.data.challenge.impl.mapper

import com.yjy.data.network.response.GetChallengeRankingResponse
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.common.Tier
import com.yjy.model.common.User

internal fun GetChallengeRankingResponse.toModel() = ChallengeRank(
    rank = rank,
    user = User(
        name = name,
        tier = Tier.getCurrentTier(bestRecordInSeconds),
    ),
    memberId = memberId,
    recentResetDateTime = recentResetDateTime.toLocalDateTime(),
    currentRecordInSeconds = 0L,
    targetDays = targetDays.toTargetDays(),
    isInActive = if (isMine) false else isInActive,
    isComplete = false,
    isMine = isMine,
)
