package com.yjy.data.user.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.database.dao.NotificationDao
import com.yjy.data.datastore.api.NotificationSettingDataSource
import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.network.request.user.RegisterFirebaseTokenRequest
import com.yjy.data.user.api.FcmTokenProvider
import com.yjy.data.user.api.UserRepository
import com.yjy.data.user.impl.mapper.toModel
import com.yjy.data.user.impl.mediator.NotificationRemoteMediator
import com.yjy.model.common.notification.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val notificationSettingDataSource: NotificationSettingDataSource,
    private val notificationDao: NotificationDao,
    private val fcmTokenProvider: FcmTokenProvider,
    private val userDataSource: UserDataSource,
) : UserRepository {

    override val timeDiff: Flow<Long> = userPreferencesDataSource.timeDiff
    override val notificationSettings: Flow<Int> = notificationSettingDataSource.notificationSettings
    override val mutedChallengeBoardIds: Flow<List<Int>> = notificationSettingDataSource.mutedChallengeBoards
    override val mutedCommunityPostIds: Flow<List<Int>> = notificationSettingDataSource.mutedCommunityPosts

    override suspend fun syncTime(): NetworkResult<Unit> = userDataSource.syncTime()

    override suspend fun getUserName(): NetworkResult<String> =
        userDataSource.getUserName().map { it.userName }

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
            val currentTimeDiff = timeDiff.first()
            pagingData.map { notification ->
                notification.toModel().copy(
                    createdDateTime = notification.createdDateTime.plusSeconds(currentTimeDiff),
                )
            }
        }
    }

    override suspend fun deleteNotification(notificationId: Int): NetworkResult<Unit> =
        userDataSource.deleteNotification(notificationId)
            .onSuccess {
                notificationDao.deleteById(notificationId)
            }

    override suspend fun deleteAllNotifications(): NetworkResult<Unit> =
        userDataSource.deleteNotifications()
            .onSuccess {
                notificationDao.deleteAll()
            }

    override suspend fun registerFcmToken() {
        val currentToken: String = fcmTokenProvider.getToken()
        val lastRegisteredToken: String? = userPreferencesDataSource.getFcmToken()

        if (currentToken != lastRegisteredToken) {
            userDataSource.registerFirebaseToken(RegisterFirebaseTokenRequest(currentToken))
                .onSuccess { userPreferencesDataSource.setFcmToken(currentToken) }
                .onFailure { Timber.d("registerFcmToken() failed") }
        }
    }

    override suspend fun getNotificationSettings(): Int =
        notificationSettingDataSource.getNotificationSettings()

    override suspend fun getMutedChallengeBoards(): List<Int> =
        notificationSettingDataSource.getMutedChallengeBoards()

    override suspend fun getMutedCommunityPosts(): List<Int> =
        notificationSettingDataSource.getMutedCommunityPosts()

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
        userPreferencesDataSource.setFcmToken(null)
        notificationSettingDataSource.clear()
        notificationDao.deleteAll()
    }

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
    }
}
