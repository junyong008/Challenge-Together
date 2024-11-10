package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val timeDiff: Flow<Long>
    suspend fun setTimeDiff(diff: Long)
    suspend fun getFcmToken(): String?
    suspend fun setFcmToken(token: String?)
}
