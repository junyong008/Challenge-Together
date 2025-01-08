package com.yjy.data.network.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("kakaoId")
    val kakaoId: String,
    @SerialName("googleId")
    val googleId: String,
    @SerialName("naverId")
    val naverId: String,
    @SerialName("guestId")
    val guestId: String,
)
