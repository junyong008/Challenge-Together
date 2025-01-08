package com.yjy.data.challenge.impl.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.database.dao.ChallengePostDao
import com.yjy.data.database.model.ChallengePostEntity
import com.yjy.data.network.datasource.ChallengePostDataSource

@OptIn(ExperimentalPagingApi::class)
internal class ChallengePostRemoteMediator(
    private val challengeId: Int,
    private val challengePostDao: ChallengePostDao,
    private val challengePostDataSource: ChallengePostDataSource,
) : RemoteMediator<Int, ChallengePostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChallengePostEntity>,
    ): MediatorResult {
        val lastPostId = when (loadType) {
            LoadType.REFRESH -> REFRESH_LAST_ID
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> challengePostDao.getOldestPostId(challengeId) ?: REFRESH_LAST_ID
        }

        val response = challengePostDataSource.getChallengePosts(
            challengeId = challengeId,
            lastPostId = lastPostId,
            limit = state.config.pageSize,
        )

        return when (response) {
            is NetworkResult.Success -> {
                val result = response.data.map { it.toEntity(challengeId) }
                if (loadType == LoadType.REFRESH) {
                    challengePostDao.deleteAll()
                }
                challengePostDao.insertAll(result)

                MediatorResult.Success(endOfPaginationReached = result.size != state.config.pageSize)
            }

            is NetworkResult.Failure -> MediatorResult.Error(response.safeThrowable())
        }
    }

    private companion object {
        const val REFRESH_LAST_ID = Int.MAX_VALUE
    }
}
