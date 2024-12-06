package com.yjy.data.network.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailRequest(
    @SerialName("email")
    val email: String,
)
