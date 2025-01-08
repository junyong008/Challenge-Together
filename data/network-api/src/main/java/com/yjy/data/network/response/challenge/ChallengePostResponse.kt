package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengePostResponse(
    @SerialName("postId")
    val postId: Int,
    @SerialName("tempId")
    val tempId: Int,
    @SerialName("challengeId")
    val challengeId: Int,
    @SerialName("content")
    val content: String,
    @SerialName("writerIndex")
    val writerIndex: Int,
    @SerialName("writerName")
    val writerName: String,
    @SerialName("writerBestRecordInSeconds")
    val writerBestRecordInSeconds: Long,
    @SerialName("writtenDateTime")
    val writtenDateTime: String,
    @SerialName("isAuthor")
    val isAuthor: Boolean,
)
