package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetProfileResponse(
    @SerialName("NAME")
    val userName: String,
    @SerialName("UNVIEWEDNOTICOUNT")
    val unViewedNotificationCount: Int,
)
