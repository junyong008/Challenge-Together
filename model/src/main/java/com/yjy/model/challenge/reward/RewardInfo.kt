package com.yjy.model.challenge.reward

data class RewardInfo(
    val challengeId: Int,
    val amount: Double,
    val amountPerSec: Double,
    val rewardUnit: RewardUnit,
    val rewards: List<Reward>,
)
