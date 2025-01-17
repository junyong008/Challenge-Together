package com.yjy.data.network.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailLoginRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
)
