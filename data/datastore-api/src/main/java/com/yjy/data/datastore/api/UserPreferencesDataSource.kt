package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val timeDiff: Flow<Long>
    val userName: Flow<String>
    val unViewedNotificationCount: Flow<Int>
    val completedChallengeTitles: Flow<List<String>>
    suspend fun setTimeDiff(diff: Long)
    suspend fun setUserName(name: String?)
    suspend fun setUnViewedNotificationCount(count: Int?)
    suspend fun setCompletedChallengeTitles(titles: List<String>)
    suspend fun addCompletedChallengeTitles(newTitles: List<String>)
}
