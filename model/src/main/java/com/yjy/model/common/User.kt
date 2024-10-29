package com.yjy.model.common

data class User(
    val id: String,
    val name: String,
    val tier: Tier?,
)
