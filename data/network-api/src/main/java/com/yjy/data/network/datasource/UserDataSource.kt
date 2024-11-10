package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.RegisterFirebaseTokenRequest
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse

interface UserDataSource {
    suspend fun getUserName(): NetworkResult<GetNameResponse>
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>
    suspend fun registerFirebaseToken(request: RegisterFirebaseTokenRequest): NetworkResult<Unit>
}
