package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.user.ChangeUserNameRequest
import com.yjy.data.network.request.user.RegisterFirebaseTokenRequest
import com.yjy.data.network.response.user.CheckBanResponse
import com.yjy.data.network.response.user.GetNameResponse
import com.yjy.data.network.response.user.GetNotificationsResponse
import com.yjy.data.network.response.user.GetRemainTimeForChangeNameResponse
import com.yjy.data.network.response.user.GetUnViewedNotificationCountResponse
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    fun getRemoteAppVersion(): Flow<String>
    fun getMaintenanceEndTime(): Flow<String>
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun getUserName(): NetworkResult<GetNameResponse>
    suspend fun checkBan(identifier: String): NetworkResult<CheckBanResponse?>
    suspend fun getRemainTimeForChangeName(): NetworkResult<GetRemainTimeForChangeNameResponse>
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>
    suspend fun getNotifications(lastNotificationId: Int, limit: Int): NetworkResult<List<GetNotificationsResponse>>
    suspend fun deleteNotification(notificationId: Int): NetworkResult<Unit>
    suspend fun deleteNotifications(): NetworkResult<Unit>
    suspend fun changeUserName(request: ChangeUserNameRequest): NetworkResult<Unit>
    suspend fun registerFirebaseToken(request: RegisterFirebaseTokenRequest): NetworkResult<Unit>
}
