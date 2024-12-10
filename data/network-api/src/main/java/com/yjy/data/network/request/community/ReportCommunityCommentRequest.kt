package com.yjy.data.network.request.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportCommunityCommentRequest(
    @SerialName("commentId")
    val commentId: Int,
    @SerialName("reason")
    val reason: String,
)
