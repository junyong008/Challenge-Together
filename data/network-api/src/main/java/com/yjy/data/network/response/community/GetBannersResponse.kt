package com.yjy.data.network.response.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetBannersResponse(
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("image_width")
    val imageWidth: Int,
    @SerialName("image_height")
    val imageHeight: Int,
    @SerialName("click_url")
    val clickUrl: String,
    @SerialName("lang")
    val language: String,
)
