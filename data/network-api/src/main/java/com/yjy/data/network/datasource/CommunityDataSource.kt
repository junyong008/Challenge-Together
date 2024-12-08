package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.community.AddCommunityPostRequest
import com.yjy.data.network.response.community.GetPostsResponse

interface CommunityDataSource {
    suspend fun addPost(request: AddCommunityPostRequest): NetworkResult<Unit>
    suspend fun getPosts(query: String, lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getBookmarkedPosts(lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getAuthoredPosts(lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getCommentedPosts(lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
}
