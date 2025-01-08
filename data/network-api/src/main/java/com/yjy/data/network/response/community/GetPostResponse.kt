package com.yjy.data.network.response.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPostResponse(
    @SerialName("post")
    val post: PostResponse,
    @SerialName("comments")
    val comments: List<CommentResponse>,
)
