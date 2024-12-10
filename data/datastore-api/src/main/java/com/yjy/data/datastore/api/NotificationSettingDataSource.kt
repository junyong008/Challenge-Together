package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface NotificationSettingDataSource {
    val notificationSettings: Flow<Int>
    val mutedChallengeBoards: Flow<List<Int>>
    val mutedCommunityPosts: Flow<List<Int>>

    suspend fun getNotificationSettings(): Int
    suspend fun setNotificationSetting(flag: Int, enabled: Boolean)
    suspend fun getMutedChallengeBoards(): List<Int>
    suspend fun getMutedCommunityPosts(): List<Int>
    suspend fun addMutedChallengeBoard(challengeId: Int)
    suspend fun addMutedCommunityPost(postId: Int)
    suspend fun removeMutedChallengeBoard(challengeId: Int)
    suspend fun removeMutedCommunityPost(postId: Int)
    suspend fun clear()
}
