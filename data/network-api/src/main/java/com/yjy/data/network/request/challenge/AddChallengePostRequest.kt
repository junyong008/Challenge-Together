package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddChallengePostRequest(
    @SerialName("content")
    val content: String,
    @SerialName("tempId")
    val tempId: Int,
)
