package com.yjy.data.challenge.impl.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toTogetherEntity
import com.yjy.data.database.dao.TogetherChallengeDao
import com.yjy.data.database.model.TogetherChallengeEntity
import com.yjy.data.network.datasource.WaitingChallengeDataSource
import com.yjy.model.challenge.core.Category

@OptIn(ExperimentalPagingApi::class)
internal class TogetherChallengeRemoteMediator(
    private val query: String,
    private val category: Category,
    private val togetherChallengeDao: TogetherChallengeDao,
    private val waitingChallengeDataSource: WaitingChallengeDataSource,
) : RemoteMediator<Int, TogetherChallengeEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TogetherChallengeEntity>,
    ): MediatorResult {
        val lastChallengeId = when (loadType) {
            LoadType.REFRESH -> REFRESH_LAST_ID
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> togetherChallengeDao.getOldestChallengeId() ?: REFRESH_LAST_ID
        }

        val response = waitingChallengeDataSource.getTogetherChallenges(
            category = category.toRequestString(),
            query = query,
            lastChallengeId = lastChallengeId,
            limit = state.config.pageSize,
        )

        return when (response) {
            is NetworkResult.Success -> {
                val result = response.data.map { it.toTogetherEntity() }
                if (loadType == LoadType.REFRESH) {
                    togetherChallengeDao.replaceAll(result)
                } else {
                    togetherChallengeDao.insertAll(result)
                }

                MediatorResult.Success(endOfPaginationReached = result.size != state.config.pageSize)
            }

            is NetworkResult.Failure -> MediatorResult.Error(response.safeThrowable())
        }
    }

    private companion object {
        const val REFRESH_LAST_ID = Int.MAX_VALUE
    }
}
