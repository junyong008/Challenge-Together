package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class ChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : ChallengeDataSource {

    override suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse> =
        challengeTogetherService.addChallenge(request)
}
