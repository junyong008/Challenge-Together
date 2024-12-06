package com.yjy.data.network.request.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddChallengeRequest(
    @SerialName("category")
    val category: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("startDateTime")
    val startDateTime: String,
    @SerialName("targetDays")
    val targetDays: String,
    @SerialName("maxParticipants")
    val maxParticipants: String,
    @SerialName("roomPassword")
    val password: String,
)
