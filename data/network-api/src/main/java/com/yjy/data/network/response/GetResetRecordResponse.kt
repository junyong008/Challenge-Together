package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetResetRecordResponse(
    @SerialName("RESETDATE")
    val resetDateTime: String,
    @SerialName("ABSTINENCETIME")
    val recordInSeconds: Long,
    @SerialName("RESETMEMO")
    val content: String,
)