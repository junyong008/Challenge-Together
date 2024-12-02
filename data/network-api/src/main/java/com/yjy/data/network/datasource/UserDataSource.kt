package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.RegisterFirebaseTokenRequest
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetNotificationsResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse

interface UserDataSource {
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun getUserName(): NetworkResult<GetNameResponse>
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>
    suspend fun getNotifications(lastNotificationId: Int, limit: Int): NetworkResult<List<GetNotificationsResponse>>
    suspend fun deleteNotification(notificationId: Int): NetworkResult<Unit>
    suspend fun deleteNotifications(): NetworkResult<Unit>
    suspend fun registerFirebaseToken(request: RegisterFirebaseTokenRequest): NetworkResult<Unit>
}
