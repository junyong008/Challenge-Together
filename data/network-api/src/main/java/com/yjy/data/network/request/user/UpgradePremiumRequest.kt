package com.yjy.data.network.request.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpgradePremiumRequest(
    @SerialName("purchaseToken")
    val purchaseToken: String,
)
