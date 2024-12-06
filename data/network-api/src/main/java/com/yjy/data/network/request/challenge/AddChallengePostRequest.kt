package com.yjy.data.network.request.challenge

import kotlinx.serialization.Serializable

@Serializable
data class AddChallengePostRequest(
    val content: String,
    val tempId: Int,
)
