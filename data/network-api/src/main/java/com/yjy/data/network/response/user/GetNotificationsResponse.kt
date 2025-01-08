package com.yjy.data.network.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetNotificationsResponse(
    @SerialName("USERALARM_IDX")
    val notificationId: Int,
    @SerialName("TITLE")
    val header: String,
    @SerialName("CONTENT")
    val body: String,
    @SerialName("CREATEDATE")
    val createdDateTime: String,
    @SerialName("TYPE")
    val type: String,
    @SerialName("LINKIDX")
    val linkId: Int,
)
