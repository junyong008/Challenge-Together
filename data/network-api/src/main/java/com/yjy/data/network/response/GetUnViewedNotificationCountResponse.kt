package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUnViewedNotificationCountResponse(
    @SerialName("UNVIEWEDNOTICOUNT")
    val unViewedNotificationCount: Int,
)
