package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.response.challenge.GetWaitingChallengeDetailResponse
import com.yjy.data.network.response.challenge.WaitingChallengeResponse

interface WaitingChallengeDataSource {
    suspend fun deleteWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun startWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun joinWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun leaveWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun getTogetherChallenges(
        category: String,
        query: String,
        lastChallengeId: Int,
        limit: Int,
    ): NetworkResult<List<WaitingChallengeResponse>>
    suspend fun getWaitingChallengeDetail(
        challengeId: Int,
        password: String,
    ): NetworkResult<GetWaitingChallengeDetailResponse>
}
