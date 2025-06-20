package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddReasonToStartChallengeRequest(
    @SerialName("challengeId")
    val challengeId: Int,
    @SerialName("reason")
    val reason: String,
)
