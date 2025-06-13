package com.yjy.data.network.response.challenge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetChallengeRankingResponse(
    @SerialName("ROOMMEM_IDX")
    val memberId: Int,
    @SerialName("RANK")
    val rank: Int,
    @SerialName("SCORERANK")
    val scoreRank: Int,
    @SerialName("NAME")
    val name: String,
    @SerialName("RECENTSTARTTIME")
    val recentResetDateTime: String,
    @SerialName("BESTTIME")
    val bestRecordInSeconds: Long,
    @SerialName("SCORE")
    val score: Int,
    @SerialName("ISINACTIVE")
    val isInActive: Boolean,
    @SerialName("ISMINE")
    val isMine: Boolean,
    @SerialName("ENDTIME")
    val targetDays: Int,
)
