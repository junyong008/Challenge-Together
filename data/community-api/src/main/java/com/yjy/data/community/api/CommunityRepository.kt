package com.yjy.data.community.api

import androidx.paging.PagingData
import com.yjy.common.network.NetworkResult
import com.yjy.model.common.ReportReason
import com.yjy.model.community.Banner
import com.yjy.model.community.DetailedCommunityPost
import com.yjy.model.community.SimpleCommunityPost
import com.yjy.model.community.SimpleCommunityPostType
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    suspend fun addPost(content: String, languageCode: String): NetworkResult<Unit>
    suspend fun addComment(postId: Int, content: String, parentCommentId: Int): NetworkResult<Unit>
    suspend fun editPost(postId: Int, content: String): NetworkResult<Unit>
    suspend fun getPost(postId: Int): NetworkResult<DetailedCommunityPost>
    fun getPosts(
        query: String,
        languageCode: String,
        postType: SimpleCommunityPostType,
    ): Flow<PagingData<SimpleCommunityPost>>
    suspend fun getBanners(): NetworkResult<List<Banner>>
    suspend fun toggleBookmark(postId: Int): NetworkResult<Unit>
    suspend fun toggleLike(postId: Int): NetworkResult<Unit>
    suspend fun deletePost(postId: Int): NetworkResult<Unit>
    suspend fun deleteComment(commentId: Int): NetworkResult<Unit>
    suspend fun reportPost(postId: Int, reason: ReportReason): NetworkResult<Unit>
    suspend fun reportComment(commentId: Int, reason: ReportReason): NetworkResult<Unit>
}
