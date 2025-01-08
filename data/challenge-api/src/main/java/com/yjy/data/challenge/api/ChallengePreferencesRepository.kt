package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.UserRecord
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow

interface ChallengePreferencesRepository {
    val timeChangedFlow: Flow<Unit>
    val localTier: Flow<Tier>
    val sortOrder: Flow<SortOrder>
    val recentCompletedChallengeTitles: Flow<List<String>>

    suspend fun getRecords(): NetworkResult<UserRecord>
    suspend fun getRemoteTier(): NetworkResult<Tier>
    suspend fun setLocalTier(tier: Tier)
    suspend fun setSortOrder(order: SortOrder)
    suspend fun clearRecentCompletedChallenges()
    suspend fun clearLocalData()
}
