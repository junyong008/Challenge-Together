package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetResetRecordResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse

interface ChallengeDataSource {
    suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse>
    suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: String): NetworkResult<Unit>
    suspend fun editChallengeCategory(challengeId: String, category: String): NetworkResult<Unit>
    suspend fun editChallengeTitleDescription(request: EditChallengeTitleDescriptionRequest): NetworkResult<Unit>
    suspend fun editChallengeTargetDays(challengeId: String, targetDays: String): NetworkResult<Unit>
    suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse>
    suspend fun getStartedChallengeDetail(
        challengeId: String,
    ): NetworkResult<GetStartedChallengeDetailResponse>
    suspend fun getResetRecords(
        challengeId: String,
    ): NetworkResult<List<GetResetRecordResponse>>
}
