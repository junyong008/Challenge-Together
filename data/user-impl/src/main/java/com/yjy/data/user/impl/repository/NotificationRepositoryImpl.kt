package com.yjy.data.user.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onSuccess
import com.yjy.data.database.dao.NotificationDao
import com.yjy.data.datastore.api.NotificationSettingDataSource
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.user.api.NotificationRepository
import com.yjy.data.user.impl.mapper.toModel
import com.yjy.data.user.impl.mediator.NotificationRemoteMediator
import com.yjy.model.common.notification.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationSettingDataSource: NotificationSettingDataSource,
    private val notificationDao: NotificationDao,
    private val userDataSource: UserDataSource,
) : NotificationRepository {

    override val notificationSettings: Flow<Int> = notificationSettingDataSource.notificationSettings
    override val mutedChallengeBoardIds: Flow<List<Int>> = notificationSettingDataSource.mutedChallengeBoards
    override val mutedCommunityPostIds: Flow<List<Int>> = notificationSettingDataSource.mutedCommunityPosts

    override suspend fun getUnViewedNotificationCount(): NetworkResult<Int> =
        userDataSource.getUnViewedNotificationCount().map { it.unViewedNotificationCount }

    @OptIn(ExperimentalPagingApi::class)
    override fun getNotifications(): Flow<PagingData<Notification>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                initialLoadSize = PAGING_INITIAL_LOAD_SIZE,
                prefetchDistance = PAGING_PREFETCH_DISTANCE,
                enablePlaceholders = true,
            ),
            remoteMediator = NotificationRemoteMediator(
                userDataSource = userDataSource,
                notificationDao = notificationDao,
            ),
            pagingSourceFactory = {
                notificationDao.pagingSource()
            },
        )

        return pager.flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

    override suspend fun deleteNotification(notificationId: Int): NetworkResult<Unit> =
        userDataSource.deleteNotification(notificationId)
            .onSuccess { notificationDao.deleteById(notificationId) }

    override suspend fun deleteAllNotifications(): NetworkResult<Unit> =
        userDataSource.deleteNotifications()
            .onSuccess { notificationDao.deleteAll() }

    override suspend fun setNotificationSetting(flag: Int, enabled: Boolean) {
        notificationSettingDataSource.setNotificationSetting(flag, enabled)
    }

    override suspend fun muteChallengeBoardNotification(challengeId: Int) {
        notificationSettingDataSource.addMutedChallengeBoard(challengeId)
    }

    override suspend fun muteCommunityPostNotification(postId: Int) {
        notificationSettingDataSource.addMutedCommunityPost(postId)
    }

    override suspend fun unMuteChallengeBoardNotification(challengeId: Int) {
        notificationSettingDataSource.removeMutedChallengeBoard(challengeId)
    }

    override suspend fun unMuteCommunityPostNotification(postId: Int) {
        notificationSettingDataSource.removeMutedCommunityPost(postId)
    }

    override suspend fun clearLocalData() {
        notificationSettingDataSource.clear()
        notificationDao.deleteAll()
    }

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
    }
}
