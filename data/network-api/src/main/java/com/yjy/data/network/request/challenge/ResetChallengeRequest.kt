package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetChallengeRequest(
    @SerialName("challengeId")
    val challengeId: Int,
    @SerialName("resetDateTime")
    val resetDateTime: String,
    @SerialName("resetMemo")
    val resetMemo: String,
)
