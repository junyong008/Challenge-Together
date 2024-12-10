package com.yjy.data.network.request.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportCommunityPostRequest(
    @SerialName("postId")
    val postId: Int,
    @SerialName("reason")
    val reason: String,
)
