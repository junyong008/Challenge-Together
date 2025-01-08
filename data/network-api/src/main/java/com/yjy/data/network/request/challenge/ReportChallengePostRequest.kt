package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportChallengePostRequest(
    @SerialName("postId")
    val postId: Int,
    @SerialName("reason")
    val reason: String,
)
