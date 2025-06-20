package com.yjy.model.challenge.reward

data class Reward(
    val id: Int,
    val name: String,
    val price: Double,
    val relateUrl: String,
    val hasPurchased: Boolean,
)
