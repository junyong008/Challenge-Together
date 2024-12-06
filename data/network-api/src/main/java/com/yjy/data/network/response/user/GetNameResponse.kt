package com.yjy.data.network.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetNameResponse(
    @SerialName("NAME")
    val userName: String,
)
