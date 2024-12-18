package com.yjy.data.user.api

import com.yjy.common.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val timeDiff: Flow<Long>
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun getUserName(): NetworkResult<String>
    suspend fun getRemainSecondsForChangeName(): NetworkResult<Long>
    suspend fun changeUserName(name: String): NetworkResult<Unit>
    suspend fun registerFcmToken()
    suspend fun clearLocalData()
}
