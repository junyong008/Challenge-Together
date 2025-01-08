package com.yjy.data.challenge.api

import androidx.paging.PagingData
import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.ChallengePost
import com.yjy.model.common.ReportReason
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ChallengePostRepository {
    fun getChallengePosts(challengeId: Int): Flow<PagingData<ChallengePost>>
    fun getLatestChallengePost(challengeId: Int): Flow<ChallengePost?>
    fun observeChallengePostUpdates(challengeId: Int): Flow<Unit>
    suspend fun addChallengePost(challengeId: Int, content: String, tempWrittenDateTime: LocalDateTime)
    suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit>
    suspend fun reportChallengePost(postId: Int, reportReason: ReportReason): NetworkResult<Unit>
    suspend fun clearLocalData()
}
