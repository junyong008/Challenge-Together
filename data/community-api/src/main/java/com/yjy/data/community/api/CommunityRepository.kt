package com.yjy.data.community.api

import androidx.paging.PagingData
import com.yjy.common.network.NetworkResult
import com.yjy.model.community.SimpleCommunityPost
import com.yjy.model.community.SimpleCommunityPostType
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    suspend fun addPost(content: String): NetworkResult<Unit>
    fun getPosts(query: String, postType: SimpleCommunityPostType): Flow<PagingData<SimpleCommunityPost>>
}
