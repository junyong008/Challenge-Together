package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetStartReasonResponse(
    @SerialName("ROOMMEMREASON_IDX")
    val id: Int,
    @SerialName("REASON")
    val reason: String,
)
