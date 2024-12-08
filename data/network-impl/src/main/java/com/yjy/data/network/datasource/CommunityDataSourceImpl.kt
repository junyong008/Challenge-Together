package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.community.AddCommunityPostRequest
import com.yjy.data.network.response.community.GetPostsResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class CommunityDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : CommunityDataSource {

    override suspend fun addPost(request: AddCommunityPostRequest): NetworkResult<Unit> =
        challengeTogetherService.addPost(request)

    override suspend fun getPosts(
        query: String,
        lastPostId: Int,
        limit: Int,
    ): NetworkResult<List<GetPostsResponse>> = challengeTogetherService.getPosts(
        query = query,
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
}
