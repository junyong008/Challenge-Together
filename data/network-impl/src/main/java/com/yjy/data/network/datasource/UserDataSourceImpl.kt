package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.user.ChangeUserNameRequest
import com.yjy.data.network.request.user.LinkAccountRequest
import com.yjy.data.network.request.user.RegisterFirebaseTokenRequest
import com.yjy.data.network.response.user.CheckBanResponse
import com.yjy.data.network.response.user.GetAccountTypeResponse
import com.yjy.data.network.response.user.GetNameResponse
import com.yjy.data.network.response.user.GetNotificationsResponse
import com.yjy.data.network.response.user.GetRemainTimeForChangeNameResponse
import com.yjy.data.network.response.user.GetUnViewedNotificationCountResponse
import com.yjy.data.network.service.ChallengeTogetherService
import com.yjy.data.network.service.FirebaseService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
    private val firebaseService: FirebaseService,
) : UserDataSource {

    override fun getRemoteAppVersion(): Flow<String> = firebaseService.getRemoteAppVersion()

    override fun getMaintenanceEndTime(): Flow<String> = firebaseService.getMaintenanceEndTime()

    override suspend fun syncTime(): NetworkResult<Unit> = challengeTogetherService.getTime()

    override suspend fun getUserName(): NetworkResult<GetNameResponse> =
        challengeTogetherService.getUserName()

    override suspend fun getAccountType(): NetworkResult<GetAccountTypeResponse> =
        challengeTogetherService.getAccountType()

    override suspend fun checkBan(identifier: String): NetworkResult<CheckBanResponse?> =
        challengeTogetherService.checkBan(identifier)

    override suspend fun getRemainTimeForChangeName(): NetworkResult<GetRemainTimeForChangeNameResponse> =
        challengeTogetherService.getRemainTimeForChangeName()

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

    override suspend fun changeUserName(request: ChangeUserNameRequest): NetworkResult<Unit> =
        challengeTogetherService.changeUserName(request)

    override suspend fun linkAccount(request: LinkAccountRequest): NetworkResult<Unit> =
        challengeTogetherService.linkAccount(request)

    override suspend fun registerFirebaseToken(request: RegisterFirebaseTokenRequest): NetworkResult<Unit> =
        challengeTogetherService.registerFirebaseToken(request)
}
