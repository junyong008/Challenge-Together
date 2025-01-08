package com.yjy.data.network.response.community

import com.yjy.data.network.response.user.UserResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPostsResponse(
    @SerialName("COMPOST_IDX")
    val postId: Int,
    @SerialName("CONTENT")
    val content: String,
    @SerialName("AUTHOR")
    val author: UserResponse,
    @SerialName("COMMENTCOUNT")
    val commentCount: Int,
    @SerialName("LIKECOUNT")
    val likeCount: Int,
    @SerialName("CREATEDATE")
    val writtenDateTime: String,
    @SerialName("MODIFYDATE")
    val modifiedDateTime: String,
    @SerialName("TYPE")
    val type: String,
)
