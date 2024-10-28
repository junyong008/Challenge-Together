package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface ChallengePreferencesDataSource {
    val timeDiff: Flow<Long>
    val completedChallengeTitles: Flow<List<String>>
    val currentTier: Flow<ChallengePreferences.Tier>
    val sortOrder: Flow<ChallengePreferences.SortOrder>
    suspend fun setTimeDiff(diff: Long)
    suspend fun setCompletedChallengeTitles(titles: List<String>)
    suspend fun addCompletedChallengeTitles(newTitles: List<String>)
    suspend fun setCurrentTier(tier: ChallengePreferences.Tier)
    suspend fun setSortOrder(order: ChallengePreferences.SortOrder)
}
