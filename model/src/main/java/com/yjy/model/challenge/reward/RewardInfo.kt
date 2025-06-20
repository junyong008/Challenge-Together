package com.yjy.model.challenge.reward

data class RewardInfo(
    val challengeId: Int,
    val amount: Double,
    val rewardUnit: RewardUnit,
)
