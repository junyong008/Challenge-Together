package com.yjy.data.community.api

import androidx.paging.PagingData
import com.yjy.model.community.SimpleCommunityPost
import com.yjy.model.community.SimpleCommunityPostType
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getPosts(query: String, postType: SimpleCommunityPostType): Flow<PagingData<SimpleCommunityPost>>
}
