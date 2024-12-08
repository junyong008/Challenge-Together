package com.yjy.data.network.request.community

import kotlinx.serialization.Serializable

@Serializable
data class AddCommunityPostRequest(
    val content: String,
)
