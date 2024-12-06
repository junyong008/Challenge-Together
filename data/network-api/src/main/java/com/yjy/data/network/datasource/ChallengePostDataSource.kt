package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.AddChallengePostRequest
import com.yjy.data.network.request.challenge.ReportChallengePostRequest
import com.yjy.data.network.response.challenge.ChallengePostResponse
import com.yjy.data.network.response.challenge.GetChallengePostsResponse
import kotlinx.coroutines.flow.Flow

interface ChallengePostDataSource {
    fun addChallengePost(request: AddChallengePostRequest)
    fun observeChallengePostUpdates(challengeId: Int): Flow<ChallengePostResponse>
    suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit>
    suspend fun reportChallengePost(request: ReportChallengePostRequest): NetworkResult<Unit>
    suspend fun getChallengePosts(
        challengeId: Int,
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetChallengePostsResponse>>
    suspend fun getChallengePostsUpdated(
        challengeId: Int,
        lastCachedPostId: Int,
    ): NetworkResult<List<GetChallengePostsResponse>>
}
