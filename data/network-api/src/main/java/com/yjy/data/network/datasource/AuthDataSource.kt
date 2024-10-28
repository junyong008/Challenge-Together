package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.EmailRequest
import com.yjy.data.network.request.SignUpRequest
import com.yjy.data.network.request.VerifyRequest

interface AuthDataSource {
    suspend fun signUp(signUpRequest: SignUpRequest): NetworkResult<Unit>
    suspend fun emailLogin(emailLoginRequest: EmailLoginRequest): NetworkResult<Unit>
    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>
    suspend fun requestVerifyCode(emailRequest: EmailRequest): NetworkResult<Unit>
    suspend fun verifyCode(verifyRequest: VerifyRequest): NetworkResult<Unit>
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): NetworkResult<Unit>
}
