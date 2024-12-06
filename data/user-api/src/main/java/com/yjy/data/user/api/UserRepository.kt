package com.yjy.data.user.api

import androidx.paging.PagingData
import com.yjy.common.network.NetworkResult
import com.yjy.model.common.notification.Notification
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val timeDiff: Flow<Long>
    val mutedChallengeBoardIds: Flow<List<Int>>
    fun getNotifications(): Flow<PagingData<Notification>>
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun getUserName(): NetworkResult<String>
    suspend fun getUnViewedNotificationCount(): NetworkResult<Int>
    suspend fun deleteNotification(notificationId: Int): NetworkResult<Unit>
    suspend fun deleteAllNotifications(): NetworkResult<Unit>
    suspend fun registerFcmToken()
    suspend fun getMutedChallengeBoards(): List<Int>
    suspend fun muteChallengeBoardNotification(challengeId: Int)
    suspend fun unMuteChallengeBoardNotification(challengeId: Int)
    suspend fun clearLocalData()
}
