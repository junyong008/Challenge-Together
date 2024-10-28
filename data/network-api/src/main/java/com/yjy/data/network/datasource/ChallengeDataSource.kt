package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetMyChallengesResponse

interface ChallengeDataSource {
    suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse>
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>
    suspend fun syncTime(): NetworkResult<Unit>
}