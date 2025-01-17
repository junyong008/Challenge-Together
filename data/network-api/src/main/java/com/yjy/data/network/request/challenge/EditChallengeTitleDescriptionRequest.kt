package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditChallengeTitleDescriptionRequest(
    @SerialName("challengeId")
    val challengeId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
)
