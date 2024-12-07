package com.yjy.data.community.impl.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.yjy.common.network.NetworkResult
import com.yjy.data.community.impl.mapper.toEntity
import com.yjy.data.community.impl.mapper.toInternalModel
import com.yjy.data.database.dao.CommunityPostDao
import com.yjy.data.database.model.CommunityPostEntity
import com.yjy.data.network.datasource.CommunityDataSource
import com.yjy.model.community.SimpleCommunityPostType

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val query: String,
    private val postType: SimpleCommunityPostType,
    private val communityDataSource: CommunityDataSource,
    private val communityPostDao: CommunityPostDao,
) : RemoteMediator<Int, CommunityPostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CommunityPostEntity>,
    ): MediatorResult {
        val lastId = when (loadType) {
            LoadType.REFRESH -> REFRESH_LAST_ID
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> communityPostDao.getOldestPostId(postType.toInternalModel()) ?: REFRESH_LAST_ID
        }

        val response = when (postType) {
            SimpleCommunityPostType.ALL -> communityDataSource.getPosts(
                query = query,
                lastPostId = lastId,
                limit = state.config.pageSize,
            )

            SimpleCommunityPostType.BOOKMARKED -> communityDataSource.getBookmarkedPosts(
                lastPostId = lastId,
                limit = state.config.pageSize,
            )

            SimpleCommunityPostType.AUTHORED -> communityDataSource.getAuthoredPosts(
                lastPostId = lastId,
                limit = state.config.pageSize,
            )

            SimpleCommunityPostType.COMMENTED -> communityDataSource.getCommentedPosts(
                lastPostId = lastId,
                limit = state.config.pageSize,
            )
        }

        return when (response) {
            is NetworkResult.Success -> {
                val result = response.data.map { it.toEntity() }
                if (loadType == LoadType.REFRESH) {
                    communityPostDao.replaceAll(result, postType.toInternalModel())
                } else {
                    communityPostDao.insertAll(result)
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
