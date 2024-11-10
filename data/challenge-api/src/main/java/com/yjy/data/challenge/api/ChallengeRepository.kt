package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ChallengeRepository {
    val timeChangedFlow: Flow<Unit>
    val currentTier: Flow<Tier>
    val sortOrder: Flow<SortOrder>
    val recentCompletedChallengeTitles: Flow<List<String>>
    val startedChallenges: Flow<List<SimpleStartedChallenge>>
    val waitingChallenges: Flow<List<SimpleWaitingChallenge>>

    suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime? = null,
        maxParticipants: Int = 1,
        roomPassword: String = "",
    ): NetworkResult<String>

    suspend fun editChallengeTitleDescription(
        challengeId: String,
        title: String,
        description: String,
    ): NetworkResult<Unit>
    suspend fun editChallengeCategory(challengeId: String, category: Category): NetworkResult<Unit>
    suspend fun editChallengeTargetDays(challengeId: String, targetDays: TargetDays): NetworkResult<Unit>

    suspend fun resetStartedChallenge(challengeId: String, memo: String): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: String): NetworkResult<Unit>
    suspend fun getStartedChallengeDetail(challengeId: String): Flow<NetworkResult<DetailedStartedChallenge>>
    suspend fun setCurrentTier(tier: Tier)
    suspend fun setSortOrder(order: SortOrder)
    suspend fun clearRecentCompletedChallenges()
    suspend fun syncChallenges(): NetworkResult<List<String>>
}
