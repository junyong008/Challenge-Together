package com.yjy.data.network.service

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.EmailRequest
import com.yjy.data.network.request.SignUpRequest
import com.yjy.data.network.request.VerifyRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetCompleteChallengesTitleResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetProfileResponse
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

    @POST("service/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): NetworkResult<Unit>

    @POST("service/add-challenge")
    suspend fun addChallenge(
        @Body request: AddChallengeRequest,
    ): NetworkResult<AddChallengeResponse>

    @POST("service/get-my-challenges")
    suspend fun getMyChallenges(): NetworkResult<List<GetMyChallengesResponse>>

    @POST("service/get-complete-challenges-title")
    suspend fun getCompleteChallengesTitle(): NetworkResult<List<GetCompleteChallengesTitleResponse>>

    @POST("service/get-profile")
    suspend fun getProfile(): NetworkResult<GetProfileResponse>

    @POST("service/sync-time")
    suspend fun syncTime(): NetworkResult<Unit>
}
