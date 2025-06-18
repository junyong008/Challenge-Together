package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.AddReasonToStartChallengeRequest
import com.yjy.data.network.request.challenge.ResetChallengeRequest
import com.yjy.data.network.response.challenge.GetChallengeProgressResponse
import com.yjy.data.network.response.challenge.GetChallengeRankingResponse
import com.yjy.data.network.response.challenge.GetResetInfoResponse
import com.yjy.data.network.response.challenge.GetStartReasonResponse
import com.yjy.data.network.response.challenge.GetStartedChallengeDetailResponse

interface StartedChallengeDataSource {
    suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit>
    suspend fun addReasonToStartChallenge(request: AddReasonToStartChallengeRequest): NetworkResult<Unit>
    suspend fun deleteReasonToStartChallenge(reasonId: Int): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun continueStartedChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun forceRemoveFromStartedChallenge(memberId: Int): NetworkResult<Unit>
    suspend fun getStartedChallengeDetail(challengeId: Int): NetworkResult<GetStartedChallengeDetailResponse>
    suspend fun getResetInfo(challengeId: Int): NetworkResult<GetResetInfoResponse>
    suspend fun getStartReasons(challengeId: Int): NetworkResult<List<GetStartReasonResponse>>
    suspend fun getChallengeProgress(challengeId: Int): NetworkResult<GetChallengeProgressResponse>
    suspend fun getChallengeRanking(challengeId: Int): NetworkResult<List<GetChallengeRankingResponse>>
}
