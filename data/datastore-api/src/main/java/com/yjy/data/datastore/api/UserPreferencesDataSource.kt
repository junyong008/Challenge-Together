package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val timeDiff: Flow<Long>
    val isPremium: Flow<Boolean>
    val premiumDialogLastShown: Flow<Long>
    suspend fun setTimeDiff(diff: Long)
    suspend fun setIsPremium(isPremium: Boolean)
    suspend fun getFcmToken(): String?
    suspend fun setFcmToken(token: String?)
    suspend fun setPremiumDialogLastShown(timestamp: Long)
}
