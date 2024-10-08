package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult

interface AuthRepository {

    suspend fun signUp(
        nickname: String,
        email: String = "",
        password: String = "",
        kakaoId: String = "",
        googleId: String = "",
        naverId: String = "",
    ): NetworkResult<Unit>

    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>

    suspend fun emailLogin(email: String, password: String): NetworkResult<Unit>
}
