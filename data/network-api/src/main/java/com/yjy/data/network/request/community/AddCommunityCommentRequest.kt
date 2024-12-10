package com.yjy.data.network.request.community

import kotlinx.serialization.Serializable

@Serializable
data class AddCommunityCommentRequest(
    val postId: Int,
    val content: String,
    val parentCommentId: Int,
)
