package com.yjy.data.network.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuestLoginRequest(
    @SerialName("guestId")
    val guestId: String,
)
