package com.yjy.data.network.request.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkAccountRequest(
    @SerialName("kakaoId")
    val kakaoId: String,
    @SerialName("googleId")
    val googleId: String,
    @SerialName("naverId")
    val naverId: String,
)
