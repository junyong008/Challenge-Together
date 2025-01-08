package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WaitingChallengeResponse(
    @SerialName("ROOM_IDX")
    val challengeId: Int,
    @SerialName("TITLE")
    val title: String,
    @SerialName("CONTENT")
    val description: String,
    @SerialName("CHALLENGETYPE")
    val category: String,
    @SerialName("ENDTIME")
    val targetDays: Int,
    @SerialName("CURRENTUSERNUM")
    val currentParticipantCount: Int,
    @SerialName("MAXUSERNUM")
    val maxParticipantCount: Int,
    @SerialName("ISPRIVATE")
    val isPrivate: Boolean,
)
