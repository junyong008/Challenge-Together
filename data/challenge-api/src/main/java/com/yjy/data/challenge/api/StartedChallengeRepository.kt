package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.ResetRecord
import com.yjy.model.challenge.SimpleStartedChallenge
import kotlinx.coroutines.flow.Flow

interface StartedChallengeRepository {
    val startedChallenges: Flow<List<SimpleStartedChallenge>>
    val completedChallenges: Flow<List<SimpleStartedChallenge>>
    suspend fun resetStartedChallenge(challengeId: Int, memo: String): NetworkResult<Unit>
    suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun forceRemoveStartedChallengeMember(memberId: Int): NetworkResult<Unit>
    suspend fun getStartedChallengeDetail(challengeId: Int): Flow<NetworkResult<DetailedStartedChallenge>>
    suspend fun getResetRecords(challengeId: Int): NetworkResult<List<ResetRecord>>
    suspend fun getChallengeRanking(challengeId: Int): Flow<NetworkResult<List<ChallengeRank>>>
}