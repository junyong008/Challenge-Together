package com.yjy.data.network.response.challenge

import com.yjy.data.network.response.user.UserResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetWaitingChallengeDetailResponse(
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
    @SerialName("PASSWD")
    val password: String,
    @SerialName("MAXUSERNUM")
    val maxParticipantCount: Int,
    @SerialName("AUTHOR")
    val author: UserResponse,
    @SerialName("PARTICIPANTS")
    val participants: List<UserResponse>,
    @SerialName("ISAUTHOR")
    val isAuthor: Boolean,
    @SerialName("ISPARTICIPATED")
    val isParticipated: Boolean,
    @SerialName("ISINACTIVE")
    val isAuthorInActive: Boolean,
)
