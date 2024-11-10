package com.yjy.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterFirebaseTokenRequest(
    @SerialName("token")
    val token: String,
)
