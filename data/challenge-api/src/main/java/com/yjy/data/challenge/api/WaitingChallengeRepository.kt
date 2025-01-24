package com.yjy.data.challenge.api

import androidx.paging.PagingData
import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.DetailedWaitingChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import kotlinx.coroutines.flow.Flow

interface WaitingChallengeRepository {
    val waitingChallenges: Flow<List<SimpleWaitingChallenge>>
    fun getTogetherChallenges(
        query: String,
        languageCode: String,
        category: Category,
    ): Flow<PagingData<SimpleWaitingChallenge>>
    suspend fun getWaitingChallengeDetail(challengeId: Int, password: String): NetworkResult<DetailedWaitingChallenge>
    suspend fun deleteWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun startWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun joinWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun leaveWaitingChallenge(challengeId: Int): NetworkResult<Unit>
    suspend fun forceRemoveAuthor(challengeId: Int): NetworkResult<Unit>
}
