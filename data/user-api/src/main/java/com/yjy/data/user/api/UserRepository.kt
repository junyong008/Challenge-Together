package com.yjy.data.user.api

import com.yjy.common.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val timeDiff: Flow<Long>
    val mutedChallengeBoardIds: Flow<List<Int>>
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun getUserName(): NetworkResult<String>
    suspend fun getUnViewedNotificationCount(): NetworkResult<Int>
    suspend fun registerFcmToken()
    suspend fun getMutedChallengeBoards(): List<Int>
    suspend fun muteChallengeBoardNotification(challengeId: Int)
    suspend fun unMuteChallengeBoardNotification(challengeId: Int)
}
