package com.yjy.model.challenge

import com.yjy.model.challenge.core.Category

data class RecoveryProgress(
    val challengeId: Int,
    val category: Category,
    val score: Int,
    val isCompletedChallenge: Boolean,
    val hasCompletedCheckIn: Boolean,
    val hasCompletedEmotionRecord: Boolean,
    val hasCompletedCommunityEngage: Boolean,
)
