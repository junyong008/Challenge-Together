package com.yjy.data.community.impl.mapper

import com.yjy.data.network.response.community.GetBannersResponse
import com.yjy.model.community.Banner

internal fun GetBannersResponse.toModel() = Banner(
    imageUrl = imageUrl,
    imageWidth = imageWidth,
    imageHeight = imageHeight,
    clickUrl = clickUrl,
    language = language,
)
