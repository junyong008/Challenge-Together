package com.yjy.data.user.impl.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.yjy.common.network.NetworkResult
import com.yjy.data.database.dao.NotificationDao
import com.yjy.data.database.model.NotificationEntity
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.user.impl.mapper.toEntity

@OptIn(ExperimentalPagingApi::class)
class NotificationRemoteMediator(
    private val userDataSource: UserDataSource,
    private val notificationDao: NotificationDao,
) : RemoteMediator<Int, NotificationEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NotificationEntity>,
    ): MediatorResult {
        val lastId = when (loadType) {
            LoadType.REFRESH -> REFRESH_LAST_ID
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> notificationDao.getOldestNotificationId() ?: REFRESH_LAST_ID
        }

        val response = userDataSource.getNotifications(
            lastNotificationId = lastId,
            limit = state.config.pageSize,
        )

        return when (response) {
            is NetworkResult.Success -> {
                val result = response.data.map { it.toEntity() }
                if (loadType == LoadType.REFRESH) {
                    notificationDao.deleteAll()
                }
                notificationDao.insertAll(result)

                MediatorResult.Success(endOfPaginationReached = result.size != state.config.pageSize)
            }

            is NetworkResult.Failure -> MediatorResult.Error(response.safeThrowable())
        }
    }

    private companion object {
        const val REFRESH_LAST_ID = Int.MAX_VALUE
    }
}
