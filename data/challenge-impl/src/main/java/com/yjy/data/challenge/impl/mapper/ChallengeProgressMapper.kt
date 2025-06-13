package com.yjy.data.challenge.impl.mapper

import com.yjy.data.network.response.challenge.GetChallengeProgressResponse
import com.yjy.model.challenge.RecoveryProgress

internal fun GetChallengeProgressResponse.toModel() = RecoveryProgress(
    challengeId = challengeId,
    category = category.toCategory(),
    score = score,
    isCompletedChallenge = isCompletedChallenge,
    hasCompletedCheckIn = hasCompletedCheckIn,
    hasCompletedEmotionRecord = hasCompletedEmotionRecord,
    hasCompletedCommunityEngage = hasCompletedCommunityEngage,
)
