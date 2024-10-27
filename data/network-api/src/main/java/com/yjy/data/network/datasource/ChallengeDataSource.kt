package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetCompleteChallengesTitleResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetProfileResponse

interface ChallengeDataSource {

    suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse>
    suspend fun getProfile(): NetworkResult<GetProfileResponse>
    suspend fun getMyChallenges(): NetworkResult<List<GetMyChallengesResponse>>
    suspend fun getCompleteChallengesTitle(): NetworkResult<List<GetCompleteChallengesTitleResponse>>
    suspend fun syncTime(): NetworkResult<Unit>
}