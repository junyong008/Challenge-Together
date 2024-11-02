package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.WaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ChallengeRepository {
    val currentTier: Flow<Tier>
    val sortOrder: Flow<SortOrder>
    val recentCompletedChallengeTitles: Flow<List<String>>
    val startedChallenges: Flow<List<StartedChallenge>>
    val waitingChallenges: Flow<List<WaitingChallenge>>

    suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime? = null,
        maxParticipants: Int = 1,
        roomPassword: String = "",
    ): NetworkResult<String>
    suspend fun setCurrentTier(tier: Tier)
    suspend fun setSortOrder(order: SortOrder)
    suspend fun clearRecentCompletedChallenges()
    suspend fun syncChallenges(): NetworkResult<List<String>>
}
