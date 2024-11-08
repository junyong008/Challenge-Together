package com.yjy.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetChallengeRequest(
    @SerialName("challengeId")
    val challengeId: String,
    @SerialName("resetMemo")
    val resetMemo: String,
)
