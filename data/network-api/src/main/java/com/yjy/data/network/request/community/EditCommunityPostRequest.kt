package com.yjy.data.network.request.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditCommunityPostRequest(
    @SerialName("postId")
    val postId: Int,
    @SerialName("content")
    val content: String,
)
