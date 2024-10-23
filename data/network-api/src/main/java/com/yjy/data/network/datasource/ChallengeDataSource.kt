package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse

interface ChallengeDataSource {

    suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse>
}