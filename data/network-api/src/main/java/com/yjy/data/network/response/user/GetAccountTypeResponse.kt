package com.yjy.data.network.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAccountTypeResponse(
    @SerialName("TYPE")
    val type: String,
)
