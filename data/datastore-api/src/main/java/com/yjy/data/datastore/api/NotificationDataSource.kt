package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface NotificationDataSource {
    val mutedChallengeBoards: Flow<List<Int>>
    suspend fun getMutedChallengeBoards(): List<Int>
    suspend fun addMutedChallengeBoard(challengeId: Int)
    suspend fun removeMutedChallengeBoard(challengeId: Int)
}
