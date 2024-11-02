package com.yjy.data.network.service

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.EmailRequest
import com.yjy.data.network.request.SignUpRequest
import com.yjy.data.network.request.VerifyRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse
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

    @POST("service/challenge/add-challenge")
    suspend fun addChallenge(
        @Body request: AddChallengeRequest,
    ): NetworkResult<AddChallengeResponse>

    @GET("service/challenge/get-my-challenges")
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>

    @POST("service/user/get-name")
    suspend fun getUserName(): NetworkResult<GetNameResponse>

    @GET("service/user/get-un-viewed-notification-count")
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>
}
