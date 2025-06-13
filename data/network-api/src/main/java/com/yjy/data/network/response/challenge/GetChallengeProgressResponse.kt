package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetChallengeProgressResponse(
    @SerialName("challengeId")
    val challengeId: Int,
    @SerialName("category")
    val category: String,
    @SerialName("score")
    val score: Int,
    @SerialName("isCompletedChallenge")
    val isCompletedChallenge: Boolean,
    @SerialName("hasCompletedCheckIn")
    val hasCompletedCheckIn: Boolean,
    @SerialName("hasCompletedEmotionRecord")
    val hasCompletedEmotionRecord: Boolean,
    @SerialName("hasCompletedCommunityEngage")
    val hasCompletedCommunityEngage: Boolean,
)
