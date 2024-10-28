package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMyChallengesResponse(
    @SerialName("challenges")
    val challenges: List<ChallengeResponse>,
    @SerialName("newlyCompleted")
    val newlyCompletedTitles: List<String>
)
