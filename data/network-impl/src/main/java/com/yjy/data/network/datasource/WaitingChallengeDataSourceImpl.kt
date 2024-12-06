package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.response.challenge.GetWaitingChallengeDetailResponse
import com.yjy.data.network.response.challenge.WaitingChallengeResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class WaitingChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : WaitingChallengeDataSource {

    override suspend fun deleteWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteWaitingChallenge(challengeId)

    override suspend fun startWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeTogetherService.startWaitingChallenge(challengeId)

    override suspend fun joinWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeTogetherService.joinWaitingChallenge(challengeId)

    override suspend fun leaveWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeTogetherService.leaveWaitingChallenge(challengeId)

    override suspend fun getTogetherChallenges(
        category: String,
        query: String,
        lastChallengeId: Int,
        limit: Int,
    ): NetworkResult<List<WaitingChallengeResponse>> = challengeTogetherService.getTogetherChallenges(
        category = category,
        query = query,
        lastChallengeId = lastChallengeId,
        limit = limit,
    )

    override suspend fun getWaitingChallengeDetail(
        challengeId: Int,
        password: String,
    ): NetworkResult<GetWaitingChallengeDetailResponse> = challengeTogetherService.getWaitingChallengeDetail(
        challengeId = challengeId,
        password = password,
    )
}
