package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetResetInfoResponse(
    @SerialName("isComplete")
    val isCompleted: Boolean,
    @SerialName("resetRecords")
    val resetRecords: List<ResetRecordResponse>,
)
