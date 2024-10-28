package com.yjy.data.user.api

import com.yjy.common.network.NetworkResult

interface UserRepository {
    suspend fun getUserName(): NetworkResult<String>
    suspend fun getUnViewedNotificationCount(): NetworkResult<Int>
}