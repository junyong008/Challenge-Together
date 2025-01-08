package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteChallengeRequest(
    @SerialName("challengeId")
    val challengeId: Int,
)
