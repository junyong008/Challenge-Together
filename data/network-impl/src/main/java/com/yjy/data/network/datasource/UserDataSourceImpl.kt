package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.RegisterFirebaseTokenRequest
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetNotificationsResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class UserDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : UserDataSource {

    override suspend fun syncTime(): NetworkResult<Unit> = challengeTogetherService.getTime()

    override suspend fun getUserName(): NetworkResult<GetNameResponse> =
        challengeTogetherService.getUserName()

    override suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse> =
        challengeTogetherService.getUnViewedNotificationCount()

    override suspend fun getNotifications(
        lastNotificationId: Int,
        limit: Int,
    ): NetworkResult<List<GetNotificationsResponse>> =
        challengeTogetherService.getNotifications(lastNotificationId, limit)

    override suspend fun deleteNotification(notificationId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteNotification(notificationId)

    override suspend fun deleteNotifications(): NetworkResult<Unit> =
        challengeTogetherService.deleteNotifications()

    override suspend fun registerFirebaseToken(request: RegisterFirebaseTokenRequest): NetworkResult<Unit> =
        challengeTogetherService.registerFirebaseToken(request)
}
