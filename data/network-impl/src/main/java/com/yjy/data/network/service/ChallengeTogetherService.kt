package com.yjy.data.network.service

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.EmailRequest
import com.yjy.data.network.request.RegisterFirebaseTokenRequest
import com.yjy.data.network.request.ReportChallengePostRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.data.network.request.SignUpRequest
import com.yjy.data.network.request.VerifyRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetChallengePostsResponse
import com.yjy.data.network.response.GetChallengeRankingResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetNotificationsResponse
import com.yjy.data.network.response.GetRecordsResponse
import com.yjy.data.network.response.GetResetRecordResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse
import com.yjy.data.network.response.GetWaitingChallengeDetailResponse
import com.yjy.data.network.response.WaitingChallengeResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ChallengeTogetherService {

    @GET("time")
    suspend fun getTime(): NetworkResult<Unit>

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
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<Unit>

    @DELETE("service/challenge/posts/delete")
    suspend fun deleteChallengePost(
        @Query("postId") postId: Int,
    ): NetworkResult<Unit>

    @DELETE("service/challenge/delete/waiting")
    suspend fun deleteWaitingChallenge(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<Unit>

    @POST("service/challenge/start")
    suspend fun startWaitingChallenge(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<Unit>

    @POST("service/challenge/join")
    suspend fun joinWaitingChallenge(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<Unit>

    @POST("service/challenge/leave")
    suspend fun leaveWaitingChallenge(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<Unit>

    @POST("service/challenge/force-remove")
    suspend fun forceRemoveFromStartedChallenge(
        @Query("memberId") memberId: Int,
    ): NetworkResult<Unit>

    @POST("service/challenge/posts/report")
    suspend fun reportChallengePost(
        @Body request: ReportChallengePostRequest,
    ): NetworkResult<Unit>

    @PATCH("service/challenge/edit/category")
    suspend fun editChallengeCategory(
        @Query("challengeId") challengeId: Int,
        @Query("category") category: String,
    ): NetworkResult<Unit>

    @PATCH("service/challenge/edit/title-description")
    suspend fun editChallengeTitleDescription(
        @Body request: EditChallengeTitleDescriptionRequest,
    ): NetworkResult<Unit>

    @PATCH("service/challenge/edit/target-days")
    suspend fun editChallengeGoal(
        @Query("challengeId") challengeId: Int,
        @Query("targetDays") targetDays: String,
    ): NetworkResult<Unit>

    @GET("service/challenge/get/records")
    suspend fun getRecords(): NetworkResult<GetRecordsResponse>

    @GET("service/challenge/get/togethers")
    suspend fun getTogetherChallenges(
        @Query("category") category: String,
        @Query("query") query: String,
        @Query("lastChallengeId") lastChallengeId: Int,
        @Query("limit") limit: Int,
    ): NetworkResult<List<WaitingChallengeResponse>>

    @GET("service/challenge/get/all")
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>

    @GET("service/challenge/get/waiting")
    suspend fun getWaitingChallengeDetail(
        @Query("challengeId") challengeId: Int,
        @Query("password") password: String,
    ): NetworkResult<GetWaitingChallengeDetailResponse>

    @GET("service/challenge/get/started")
    suspend fun getStartedChallengeDetail(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<GetStartedChallengeDetailResponse>

    @GET("service/challenge/get/reset-records")
    suspend fun getResetRecords(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<List<GetResetRecordResponse>>

    @GET("service/challenge/posts/get/list")
    suspend fun getChallengePosts(
        @Query("challengeId") challengeId: Int,
        @Query("lastPostId") lastPostId: Int,
        @Query("limit") limit: Int,
    ): NetworkResult<List<GetChallengePostsResponse>>

    @GET("service/challenge/posts/get/updated")
    suspend fun getChallengePostsUpdated(
        @Query("challengeId") challengeId: Int,
        @Query("lastCachedPostId") lastCachedPostId: Int,
    ): NetworkResult<List<GetChallengePostsResponse>>

    @GET("service/challenge/get/ranking")
    suspend fun getChallengeRanking(
        @Query("challengeId") challengeId: Int,
    ): NetworkResult<List<GetChallengeRankingResponse>>

    @GET("service/user/get/name")
    suspend fun getUserName(): NetworkResult<GetNameResponse>

    @GET("service/user/get/notifications/un-viewed-count")
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>

    @GET("service/user/get/notifications/list")
    suspend fun getNotifications(
        @Query("lastNotificationId") lastNotificationId: Int,
        @Query("limit") limit: Int,
    ): NetworkResult<List<GetNotificationsResponse>>

    @DELETE("service/user/delete/notification")
    suspend fun deleteNotification(
        @Query("notificationId") notificationId: Int,
    ): NetworkResult<Unit>

    @DELETE("service/user/delete/notifications")
    suspend fun deleteNotifications(): NetworkResult<Unit>

    @POST("service/user/fcm/register")
    suspend fun registerFirebaseToken(
        @Body request: RegisterFirebaseTokenRequest,
    ): NetworkResult<Unit>
}
