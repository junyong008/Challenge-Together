package com.yjy.data.network.response.community

import com.yjy.data.network.response.user.UserResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("COMCOMMENT_IDX")
    val commentId: Int,
    @SerialName("PARENT_IDX")
    val parentCommentId: Int,
    @SerialName("CONTENT")
    val content: String,
    @SerialName("AUTHOR")
    val author: UserResponse,
    @SerialName("CREATEDATE")
    val writtenDateTime: String,
    @SerialName("ISAUTHOR")
    val isAuthor: Boolean,
    @SerialName("ISPOSTWRITER")
    val isPostWriter: Boolean,
)
