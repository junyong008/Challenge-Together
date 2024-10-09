package com.yjy.core.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyRequest(
    @SerialName("email")
    val email: String,
    @SerialName("code")
    val verifyCode: String,
)
