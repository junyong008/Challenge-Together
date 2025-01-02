package com.yjy.data.network.request.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCommunityCommentRequest(
    @SerialName("postId")
    val postId: Int,
    @SerialName("content")
    val content: String,
    @SerialName("parentCommentId")
    val parentCommentId: Int,
)
