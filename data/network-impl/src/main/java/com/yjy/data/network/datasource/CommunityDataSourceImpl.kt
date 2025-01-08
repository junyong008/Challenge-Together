package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.community.AddCommunityCommentRequest
import com.yjy.data.network.request.community.AddCommunityPostRequest
import com.yjy.data.network.request.community.EditCommunityPostRequest
import com.yjy.data.network.request.community.ReportCommunityCommentRequest
import com.yjy.data.network.request.community.ReportCommunityPostRequest
import com.yjy.data.network.response.community.GetPostResponse
import com.yjy.data.network.response.community.GetPostsResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class CommunityDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : CommunityDataSource {

    override suspend fun addPost(request: AddCommunityPostRequest): NetworkResult<Unit> =
        challengeTogetherService.addPost(request)

    override suspend fun addComment(request: AddCommunityCommentRequest): NetworkResult<Unit> =
        challengeTogetherService.addComment(request)

    override suspend fun editPost(request: EditCommunityPostRequest): NetworkResult<Unit> =
        challengeTogetherService.editPost(request)

    override suspend fun getPosts(
        query: String,
        languageCode: String,
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetPostsResponse>> = challengeTogetherService.getPosts(
        query = query,
        languageCode = languageCode,
        lastPostId = lastPostId,
        limit = limit,
    )

    override suspend fun getBookmarkedPosts(
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetPostsResponse>> = challengeTogetherService.getBookmarkedPosts(
        lastPostId = lastPostId,
        limit = limit,
    )

    override suspend fun getAuthoredPosts(
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetPostsResponse>> = challengeTogetherService.getAuthoredPosts(
        lastPostId = lastPostId,
        limit = limit,
    )

    override suspend fun getCommentedPosts(
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetPostsResponse>> = challengeTogetherService.getCommentedPosts(
        lastPostId = lastPostId,
        limit = limit,
    )

    override suspend fun getPost(postId: Int): NetworkResult<GetPostResponse> =
        challengeTogetherService.getPost(postId)

    override suspend fun toggleBookmark(postId: Int): NetworkResult<Unit> =
        challengeTogetherService.toggleBookmark(postId)

    override suspend fun toggleLike(postId: Int): NetworkResult<Unit> =
        challengeTogetherService.toggleLike(postId)

    override suspend fun deletePost(postId: Int): NetworkResult<Unit> =
        challengeTogetherService.deletePost(postId)

    override suspend fun deleteComment(commentId: Int): NetworkResult<Unit> =
        challengeTogetherService.deleteComment(commentId)

    override suspend fun reportPost(request: ReportCommunityPostRequest): NetworkResult<Unit> =
        challengeTogetherService.reportPost(request)

    override suspend fun reportComment(request: ReportCommunityCommentRequest): NetworkResult<Unit> =
        challengeTogetherService.reportComment(request)
}
