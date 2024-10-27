package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetCompleteChallengesTitleResponse(
    @SerialName("TITLE")
    val title: String,
)
