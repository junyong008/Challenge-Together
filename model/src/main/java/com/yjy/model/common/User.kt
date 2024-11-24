package com.yjy.model.common

data class User(
    val name: String,
    val tier: Tier,
    val status: UserStatus = UserStatus.ACTIVE,
)

enum class UserStatus {
    ACTIVE,
    WITHDRAWN,
    DELETED,
}
