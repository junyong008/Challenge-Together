package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.ResetChallengeRequest
import com.yjy.data.network.response.challenge.GetChallengeRankingResponse
import com.yjy.data.network.response.challenge.GetResetRecordResponse
import com.yjy.data.network.response.challenge.GetStartedChallengeDetailResponse

interface StartedChallengeDataSource {
    suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun forceRemoveFromStartedChallenge(memberId: Int): NetworkResult<Unit>
    suspend fun getStartedChallengeDetail(challengeId: Int): NetworkResult<GetStartedChallengeDetailResponse>
    suspend fun getResetRecords(challengeId: Int): NetworkResult<List<GetResetRecordResponse>>
    suspend fun getChallengeRanking(challengeId: Int): NetworkResult<List<GetChallengeRankingResponse>>
}
