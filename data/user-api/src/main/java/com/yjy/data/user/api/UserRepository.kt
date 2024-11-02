package com.yjy.data.user.api

import com.yjy.common.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val timeDiff: Flow<Long>
    suspend fun getUserName(): NetworkResult<String>
    suspend fun getUnViewedNotificationCount(): NetworkResult<Int>
}
