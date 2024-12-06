package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.AddChallengePostRequest
import com.yjy.data.network.request.challenge.ReportChallengePostRequest
import com.yjy.data.network.response.challenge.GetChallengePostsResponse
import com.yjy.data.network.service.ChallengePostWebSocketService
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class ChallengePostDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
    private val challengePostWebSocketService: ChallengePostWebSocketService,
) : ChallengePostDataSource {

    override fun addChallengePost(request: AddChallengePostRequest) {
        challengePostWebSocketService.addPost(request)
    }

    override fun observeChallengePostUpdates(challengeId: Int) =
        challengePostWebSocketService.connectAsFlow(challengeId)

    override suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteChallengePost(postId)

    override suspend fun reportChallengePost(request: ReportChallengePostRequest): NetworkResult<Unit> =
        challengeTogetherService.reportChallengePost(request)

    override suspend fun getChallengePosts(
        challengeId: Int,
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetChallengePostsResponse>> =
        challengeTogetherService.getChallengePosts(
            challengeId = challengeId,
            lastPostId = lastPostId,
            limit = limit,
        )

    override suspend fun getChallengePostsUpdated(
        challengeId: Int,
        lastCachedPostId: Int,
    ): NetworkResult<List<GetChallengePostsResponse>> =
        challengeTogetherService.getChallengePostsUpdated(
            challengeId = challengeId,
            lastCachedPostId = lastCachedPostId,
        )
}
