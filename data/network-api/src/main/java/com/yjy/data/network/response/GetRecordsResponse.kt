package com.yjy.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRecordsResponse(
    @SerialName("BESTTIME")
    val recordInSeconds: Long,
    @SerialName("RESETCOUNT")
    val resetCount: Int,
    @SerialName("SUCCESSCOUNT")
    val successCount: Int,
    @SerialName("TRYCOUNT")
    val tryCount: Int,
)
