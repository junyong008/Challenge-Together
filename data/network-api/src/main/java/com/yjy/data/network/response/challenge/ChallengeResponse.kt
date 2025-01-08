package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeResponse(
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
    @SerialName("RECENTSTARTTIME")
    val recentResetDateTime: String,
    @SerialName("ISSTART")
    val isStarted: Boolean,
    @SerialName("ISPRIVATE")
    val isPrivate: Boolean,
    @SerialName("ISFREEMODE")
    val isFreeMode: Boolean,
    @SerialName("ISCOMPLETE")
    val isCompleted: Boolean,
)
