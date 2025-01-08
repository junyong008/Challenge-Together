package com.yjy.data.community.impl.mapper

import com.yjy.model.common.Tier
import com.yjy.model.common.User
import com.yjy.model.common.UserStatus

object UserMapper {
    private const val DELETED_USER_NAME = "(삭제)"
    private const val WITHDRAWN_USER_NAME = "(탈퇴 회원)"

    fun create(
        name: String,
        tier: Tier,
    ): User {
        val status = when (name) {
            DELETED_USER_NAME -> UserStatus.DELETED
            WITHDRAWN_USER_NAME -> UserStatus.WITHDRAWN
            else -> UserStatus.ACTIVE
        }

        return User(
            name = name,
            tier = tier,
            status = status,
        )
    }
}
