package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetChallengePostsResponse(
    @SerialName("ROOMPOST_IDX")
    val postId: Int,
    @SerialName("NAME")
    val writerName: String,
    @SerialName("BESTTIME")
    val writerBestRecordInSeconds: Long,
    @SerialName("CONTENT")
    val content: String,
    @SerialName("TIME")
    val writtenDateTime: String,
    @SerialName("ISAUTHOR")
    val isAuthor: Boolean,
)
