package com.yjy.core.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailRequest(
    @SerialName("email")
    val email: String,
)