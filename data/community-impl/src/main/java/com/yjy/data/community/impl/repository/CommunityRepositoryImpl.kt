package com.yjy.data.community.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.data.community.api.CommunityRepository
import com.yjy.data.community.impl.mapper.toInternalModel
import com.yjy.data.community.impl.mapper.toModel
import com.yjy.data.community.impl.mediator.PostRemoteMediator
import com.yjy.data.database.dao.CommunityPostDao
import com.yjy.data.network.datasource.CommunityDataSource
import com.yjy.model.community.SimpleCommunityPost
import com.yjy.model.community.SimpleCommunityPostType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CommunityRepositoryImpl @Inject constructor(
    private val communityDataSource: CommunityDataSource,
    private val communityPostDao: CommunityPostDao,
) : CommunityRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPosts(query: String, postType: SimpleCommunityPostType): Flow<PagingData<SimpleCommunityPost>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                initialLoadSize = PAGING_INITIAL_LOAD_SIZE,
                prefetchDistance = PAGING_PREFETCH_DISTANCE,
                enablePlaceholders = true,
            ),
            remoteMediator = PostRemoteMediator(
                query = query,
                postType = postType,
                communityDataSource = communityDataSource,
                communityPostDao = communityPostDao,
            ),
            pagingSourceFactory = {
                communityPostDao.pagingSource(type = postType.toInternalModel())
            },
        )

        return pager.flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
    }
}
