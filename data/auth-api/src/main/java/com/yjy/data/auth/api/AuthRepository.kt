package com.yjy.data.auth.api

import com.yjy.common.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val isLoggedIn: Flow<Boolean>
    val isSessionTokenAvailable: Flow<Boolean>

    suspend fun signUp(
        nickname: String,
        email: String = "",
        password: String = "",
        kakaoId: String = "",
        googleId: String = "",
        naverId: String = "",
    ): NetworkResult<Unit>

    suspend fun getIsLoggedIn(): Boolean
    suspend fun logout()
    suspend fun emailLogin(email: String, password: String): NetworkResult<Unit>
    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>
    suspend fun requestVerifyCode(email: String): NetworkResult<Unit>
    suspend fun verifyCode(email: String, verifyCode: String): NetworkResult<Unit>
    suspend fun changePassword(password: String): NetworkResult<Unit>
}
