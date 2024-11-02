package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse

interface UserDataSource {
    suspend fun getUserName(): NetworkResult<GetNameResponse>
    suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse>
}
