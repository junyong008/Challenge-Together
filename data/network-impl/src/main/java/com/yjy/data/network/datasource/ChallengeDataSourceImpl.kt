package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengePostRequest
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.ReportChallengePostRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetChallengePostsResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetRecordsResponse
import com.yjy.data.network.response.GetResetRecordResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse
import com.yjy.data.network.service.ChallengePostWebSocketService
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class ChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
    private val challengePostWebSocketService: ChallengePostWebSocketService,
) : ChallengeDataSource {

    override suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse> =
        challengeTogetherService.addChallenge(request)

    override suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit> =
        challengeTogetherService.resetChallenge(request)

    override suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteStartedChallenge(challengeId)

    override suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteChallengePost(postId)

    override suspend fun reportChallengePost(request: ReportChallengePostRequest): NetworkResult<Unit> =
        challengeTogetherService.reportChallengePost(request)

    override suspend fun editChallengeCategory(challengeId: Int, category: String): NetworkResult<Unit> =
        challengeTogetherService.editChallengeCategory(challengeId, category)

    override suspend fun editChallengeTitleDescription(
        request: EditChallengeTitleDescriptionRequest,
    ): NetworkResult<Unit> =
        challengeTogetherService.editChallengeTitleDescription(request)

    override suspend fun editChallengeTargetDays(challengeId: Int, targetDays: String): NetworkResult<Unit> =
        challengeTogetherService.editChallengeGoal(challengeId, targetDays)

    override suspend fun getRecords(): NetworkResult<GetRecordsResponse> =
        challengeTogetherService.getRecords()

    override suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse> =
        challengeTogetherService.getMyChallenges()

    override suspend fun getStartedChallengeDetail(
        challengeId: Int,
    ): NetworkResult<GetStartedChallengeDetailResponse> =
        challengeTogetherService.getStartedChallengeDetail(challengeId)

    override suspend fun getResetRecords(
        challengeId: Int,
    ): NetworkResult<List<GetResetRecordResponse>> = challengeTogetherService.getResetRecords(challengeId)

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

    override fun addChallengePost(request: AddChallengePostRequest) {
        challengePostWebSocketService.addPost(request)
    }

    override fun observeChallengePostUpdates(challengeId: Int) =
        challengePostWebSocketService.connectAsFlow(challengeId)
}
