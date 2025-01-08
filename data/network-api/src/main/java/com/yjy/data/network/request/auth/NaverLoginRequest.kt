package com.yjy.data.network.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NaverLoginRequest(
    @SerialName("naverId")
    val naverId: String,
)
