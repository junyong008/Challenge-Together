package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengePostRequest
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.ReportChallengePostRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.ChallengePostResponse
import com.yjy.data.network.response.GetChallengePostsResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetRecordsResponse
import com.yjy.data.network.response.GetResetRecordResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse
import kotlinx.coroutines.flow.Flow

interface ChallengeDataSource {
    suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse>
    suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit>
    suspend fun reportChallengePost(request: ReportChallengePostRequest): NetworkResult<Unit>
    suspend fun editChallengeCategory(challengeId: Int, category: String): NetworkResult<Unit>
    suspend fun editChallengeTitleDescription(request: EditChallengeTitleDescriptionRequest): NetworkResult<Unit>
    suspend fun editChallengeTargetDays(challengeId: Int, targetDays: String): NetworkResult<Unit>
    suspend fun getRecords(): NetworkResult<GetRecordsResponse>
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>
    suspend fun getStartedChallengeDetail(
        challengeId: Int,
    ): NetworkResult<GetStartedChallengeDetailResponse>
    suspend fun getResetRecords(
        challengeId: Int,
    ): NetworkResult<List<GetResetRecordResponse>>
    suspend fun getChallengePosts(
        challengeId: Int,
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetChallengePostsResponse>>
    suspend fun getChallengePostsUpdated(
        challengeId: Int,
        lastCachedPostId: Int,
    ): NetworkResult<List<GetChallengePostsResponse>>
    fun addChallengePost(request: AddChallengePostRequest)
    fun observeChallengePostUpdates(challengeId: Int): Flow<ChallengePostResponse>
}
