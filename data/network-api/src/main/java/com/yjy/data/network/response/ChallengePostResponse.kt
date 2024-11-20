package com.yjy.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class ChallengePostResponse(
    val postId: Int,
    val tempId: Int,
    val challengeId: Int,
    val content: String,
    val writerIndex: Int,
    val writerName: String,
    val writerBestRecordInSeconds: Long,
    val writtenDateTime: String,
    val isAuthor: Boolean,
)
