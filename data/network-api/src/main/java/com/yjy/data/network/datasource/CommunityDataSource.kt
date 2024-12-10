package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.community.AddCommunityCommentRequest
import com.yjy.data.network.request.community.AddCommunityPostRequest
import com.yjy.data.network.request.community.EditCommunityPostRequest
import com.yjy.data.network.request.community.ReportCommunityCommentRequest
import com.yjy.data.network.request.community.ReportCommunityPostRequest
import com.yjy.data.network.response.community.GetPostResponse
import com.yjy.data.network.response.community.GetPostsResponse

interface CommunityDataSource {
    suspend fun addPost(request: AddCommunityPostRequest): NetworkResult<Unit>
    suspend fun addComment(request: AddCommunityCommentRequest): NetworkResult<Unit>
    suspend fun editPost(request: EditCommunityPostRequest): NetworkResult<Unit>
    suspend fun getPosts(query: String, lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getBookmarkedPosts(lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getAuthoredPosts(lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getCommentedPosts(lastPostId: Int, limit: Int): NetworkResult<List<GetPostsResponse>>
    suspend fun getPost(postId: Int): NetworkResult<GetPostResponse>
    suspend fun toggleBookmark(postId: Int): NetworkResult<Unit>
    suspend fun toggleLike(postId: Int): NetworkResult<Unit>
    suspend fun deletePost(postId: Int): NetworkResult<Unit>
    suspend fun deleteComment(commentId: Int): NetworkResult<Unit>
    suspend fun reportPost(request: ReportCommunityPostRequest): NetworkResult<Unit>
    suspend fun reportComment(request: ReportCommunityCommentRequest): NetworkResult<Unit>
}
