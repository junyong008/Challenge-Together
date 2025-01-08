package com.yjy.data.network.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("NAME")
    val name: String,
    @SerialName("BESTTIME")
    val bestRecordInSeconds: Long,
)
