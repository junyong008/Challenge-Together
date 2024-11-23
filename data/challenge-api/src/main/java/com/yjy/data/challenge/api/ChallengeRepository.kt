package com.yjy.data.challenge.api

import androidx.paging.PagingData
import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.ChallengePost
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.ResetRecord
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.ReportReason
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ChallengeRepository {
    val timeChangedFlow: Flow<Unit>
    val localTier: Flow<Tier>
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
    ): NetworkResult<Int>

    suspend fun editChallengeTitleDescription(
        challengeId: Int,
        title: String,
        description: String,
    ): NetworkResult<Unit>
    suspend fun editChallengeCategory(challengeId: Int, category: Category): NetworkResult<Unit>
    suspend fun editChallengeTargetDays(challengeId: Int, targetDays: TargetDays): NetworkResult<Unit>

    fun getChallengePosts(challengeId: Int): Flow<PagingData<ChallengePost>>
    fun getLatestChallengePost(challengeId: Int): Flow<ChallengePost?>
    fun observeChallengePostUpdates(challengeId: Int): Flow<Unit>
    suspend fun reportChallengePost(postId: Int, reportReason: ReportReason): NetworkResult<Unit>
    suspend fun addChallengePost(challengeId: Int, content: String, tempWrittenDateTime: LocalDateTime)
    suspend fun resetStartedChallenge(challengeId: Int, memo: String): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit>
    suspend fun forceRemoveStartedChallengeMember(memberId: Int): NetworkResult<Unit>
    suspend fun getStartedChallengeDetail(challengeId: Int): Flow<NetworkResult<DetailedStartedChallenge>>
    suspend fun getResetRecords(challengeId: Int): NetworkResult<List<ResetRecord>>
    suspend fun getChallengeRanking(challengeId: Int): Flow<NetworkResult<List<ChallengeRank>>>
    suspend fun getRemoteTier(): NetworkResult<Tier>
    suspend fun setLocalTier(tier: Tier)
    suspend fun setSortOrder(order: SortOrder)
    suspend fun clearRecentCompletedChallenges()
    suspend fun syncChallenges(): NetworkResult<List<String>>
}
