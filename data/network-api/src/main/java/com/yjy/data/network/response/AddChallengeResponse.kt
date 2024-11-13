package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddChallengeResponse(
    @SerialName("roomIndex")
    val challengeId: Int,
)
