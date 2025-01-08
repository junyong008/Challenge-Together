package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.AddChallengeRequest
import com.yjy.data.network.request.challenge.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.response.challenge.AddChallengeResponse
import com.yjy.data.network.response.challenge.GetMyChallengesResponse
import com.yjy.data.network.response.challenge.GetRecordsResponse

interface ChallengeDataSource {
    suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse>
    suspend fun editChallengeCategory(challengeId: Int, category: String): NetworkResult<Unit>
    suspend fun editChallengeTitleDescription(request: EditChallengeTitleDescriptionRequest): NetworkResult<Unit>
    suspend fun editChallengeTargetDays(challengeId: Int, targetDays: String): NetworkResult<Unit>
    suspend fun getRecords(): NetworkResult<GetRecordsResponse>
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>
}
