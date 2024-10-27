package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetCompleteChallengesTitleResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetProfileResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class ChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : ChallengeDataSource {

    override suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse> =
        challengeTogetherService.addChallenge(request)

    override suspend fun getProfile(): NetworkResult<GetProfileResponse> =
        challengeTogetherService.getProfile()

    override suspend fun getMyChallenges(): NetworkResult<List<GetMyChallengesResponse>> =
        challengeTogetherService.getMyChallenges()

    override suspend fun getCompleteChallengesTitle(): NetworkResult<List<GetCompleteChallengesTitleResponse>> =
        challengeTogetherService.getCompleteChallengesTitle()

    override suspend fun syncTime(): NetworkResult<Unit> =
        challengeTogetherService.syncTime()
}
