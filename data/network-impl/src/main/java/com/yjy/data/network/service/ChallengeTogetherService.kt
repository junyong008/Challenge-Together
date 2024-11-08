package com.yjy.data.network.service

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.EmailRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.data.network.request.SignUpRequest
import com.yjy.data.network.request.VerifyRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ChallengeTogetherService {

    @POST("auth/login/email")
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

    @POST("auth/verify-code/request")
    suspend fun requestVerifyCode(
        @Body request: EmailRequest,
    ): NetworkResult<Unit>

    @POST("auth/verify-code/check")
    suspend fun verifyCode(
        @Body request: VerifyRequest,
    ): NetworkResult<Unit>

    @PATCH("service/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest,
    ): NetworkResult<Unit>

    @POST("service/challenge/add")
    suspend fun addChallenge(
        @Body request: AddChallengeRequest,
    ): NetworkResult<AddChallengeResponse>

    @PATCH("service/challenge/reset")
    suspend fun resetChallenge(
        @Body request: ResetChallengeRequest,
    ): NetworkResult<Unit>

    @DELETE("service/challenge/delete/started")
    suspend fun deleteStartedChallenge(
        @Query("challengeId") challengeId: String,
    ): NetworkResult<Unit>

    @PATCH("service/challenge/edit/category")
    suspend fun editChallengeCategory(
        @Query("challengeId") challengeId: String,
        @Query("category") category: String,
    ): NetworkResult<Unit>

    @PATCH("service/challenge/edit/title-description")
    suspend fun editChallengeTitleDescription(
        @Body request: EditChallengeTitleDescriptionRequest,
    ): NetworkResult<Unit>

    @PATCH("service/challenge/edit/target-days")
    suspend fun editChallengeGoal(
        @Query("challengeId") challengeId: String,
        @Query("targetDays") targetDays: String,
    ): NetworkResult<Unit>

    @GET("service/challenge/get/all")
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>

    @GET("service/challenge/get/started")
    suspend fun getStartedChallengeDetail(
        @Query("challengeId") challengeId: String,
    ): NetworkResult<GetStartedChallengeDetailResponse>

    @GET("service/user/get/name")
    suspend fun getUserName(): NetworkResult<GetNameResponse>

    @GET("service/user/get/notifications/un-viewed-count")
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>
}
