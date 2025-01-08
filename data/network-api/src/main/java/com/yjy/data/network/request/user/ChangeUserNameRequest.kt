package com.yjy.data.network.request.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangeUserNameRequest(
    @SerialName("name")
    val name: String,
)
