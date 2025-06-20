package com.yjy.model.challenge.reward

sealed interface RewardUnit {
    data object Money : RewardUnit
    data object Time : RewardUnit
    data class Custom(val unit: String) : RewardUnit
}
