package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult

interface AuthDataSource {

    suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
    ): NetworkResult<Unit>

    suspend fun emailLogin(email: String, password: String): NetworkResult<Unit>
    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>
    suspend fun requestVerifyCode(email: String): NetworkResult<Unit>
    suspend fun verifyCode(email: String, verifyCode: String): NetworkResult<Unit>
    suspend fun changePassword(password: String): NetworkResult<Unit>
}
