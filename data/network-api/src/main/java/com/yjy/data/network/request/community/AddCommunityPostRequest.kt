package com.yjy.data.network.request.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCommunityPostRequest(
    @SerialName("content")
    val content: String,
    @SerialName("languageCode")
    val languageCode: String,
)
