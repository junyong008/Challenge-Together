package com.yjy.data.network.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckBanResponse(
    @SerialName("REASON")
    val reason: String,
    @SerialName("ENDDATE")
    val endDateTime: String,
)
