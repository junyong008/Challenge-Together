package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.ResetChallengeRequest
import com.yjy.data.network.response.challenge.GetChallengeRankingResponse
import com.yjy.data.network.response.challenge.GetResetRecordResponse
import com.yjy.data.network.response.challenge.GetStartedChallengeDetailResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class StartedChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : StartedChallengeDataSource {

    override suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit> =
        challengeTogetherService.resetChallenge(request)

    override suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteStartedChallenge(challengeId)

    override suspend fun forceRemoveFromStartedChallenge(memberId: Int): NetworkResult<Unit> =
        challengeTogetherService.forceRemoveFromStartedChallenge(memberId)

    override suspend fun getStartedChallengeDetail(challengeId: Int): NetworkResult<GetStartedChallengeDetailResponse> =
        challengeTogetherService.getStartedChallengeDetail(challengeId)

    override suspend fun getResetRecords(challengeId: Int): NetworkResult<List<GetResetRecordResponse>> =
        challengeTogetherService.getResetRecords(challengeId)

    override suspend fun getChallengeRanking(challengeId: Int): NetworkResult<List<GetChallengeRankingResponse>> =
        challengeTogetherService.getChallengeRanking(challengeId)
}
