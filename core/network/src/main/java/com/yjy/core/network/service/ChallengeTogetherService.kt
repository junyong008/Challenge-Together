package com.yjy.core.network.service

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.request.EmailLoginRequest
import com.yjy.core.network.request.EmailRequest
import com.yjy.core.network.request.SignUpRequest
import com.yjy.core.network.request.VerifyRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ChallengeTogetherService {

    @POST("auth/email-login")
    suspend fun emailLogin(
        @Body request: EmailLoginRequest,
    ): NetworkResult<Unit>

    @GET("auth/check-email-duplicate")
    suspend fun checkEmailDuplicate(
        @Query("email") email: String,
    ): NetworkResult<Unit>

    @POST("auth/sign-up")
    suspend fun signUp(
        @Body request: SignUpRequest,
    ): NetworkResult<Unit>

    @POST("auth/request-verify-code")
    suspend fun requestVerifyCode(
        @Body request: EmailRequest,
    ): NetworkResult<Unit>

    @POST("auth/check-verify-code")
    suspend fun verifyCode(
        @Body request: VerifyRequest,
    ): NetworkResult<Unit>
}
