package com.yjy.data.community.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.common.network.HttpStatusCodes.NOT_FOUND
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.community.api.CommunityRepository
import com.yjy.data.community.impl.mapper.toEntity
import com.yjy.data.community.impl.mapper.toInternalModel
import com.yjy.data.community.impl.mapper.toLocalDateTime
import com.yjy.data.community.impl.mapper.toModel
import com.yjy.data.community.impl.mediator.PostRemoteMediator
import com.yjy.data.database.dao.CommunityPostDao
import com.yjy.data.database.model.CommunityPostType
import com.yjy.data.network.datasource.CommunityDataSource
import com.yjy.data.network.request.community.AddCommunityCommentRequest
import com.yjy.data.network.request.community.AddCommunityPostRequest
import com.yjy.data.network.request.community.EditCommunityPostRequest
import com.yjy.data.network.request.community.ReportCommunityCommentRequest
import com.yjy.data.network.request.community.ReportCommunityPostRequest
import com.yjy.model.common.ReportReason
import com.yjy.model.community.DetailedCommunityPost
import com.yjy.model.community.SimpleCommunityPost
import com.yjy.model.community.SimpleCommunityPostType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class CommunityRepositoryImpl @Inject constructor(
    private val communityDataSource: CommunityDataSource,
    private val communityPostDao: CommunityPostDao,
) : CommunityRepository {

    override suspend fun addPost(content: String, languageCode: String): NetworkResult<Unit> =
        communityDataSource.addPost(
            request = AddCommunityPostRequest(
                content = content,
                languageCode = languageCode,
            ),
        )

    override suspend fun addComment(
        postId: Int,
        content: String,
        parentCommentId: Int,
    ): NetworkResult<Unit> = communityDataSource.addComment(
        request = AddCommunityCommentRequest(
            postId = postId,
            content = content,
            parentCommentId = parentCommentId,
        ),
    )

    override suspend fun editPost(
        postId: Int,
        content: String,
    ): NetworkResult<Unit> = communityDataSource.editPost(
        request = EditCommunityPostRequest(postId = postId, content = content),
    )

    override suspend fun getPost(postId: Int): NetworkResult<DetailedCommunityPost> =
        communityDataSource.getPost(postId)
            .onSuccess { postDetail ->
                communityPostDao.updatePost(
                    id = postDetail.post.postId,
                    content = postDetail.post.content,
                    writerName = postDetail.post.author.name,
                    writerBestRecordInSeconds = postDetail.post.author.bestRecordInSeconds,
                    commentCount = postDetail.post.commentCount,
                    likeCount = postDetail.post.likeCount,
                    writtenDateTime = postDetail.post.writtenDateTime.toLocalDateTime(),
                    modifiedDateTime = postDetail.post.modifiedDateTime.toLocalDateTime(),
                )

                if (!postDetail.post.isBookmarked) {
                    communityPostDao.deleteByType(postDetail.post.postId, CommunityPostType.BOOKMARKED)
                } else {
                    communityPostDao.insert(postDetail.post.toEntity(CommunityPostType.BOOKMARKED))
                }

                if (postDetail.comments.none { it.isAuthor }) {
                    communityPostDao.deleteByType(postDetail.post.postId, CommunityPostType.COMMENTED)
                } else {
                    communityPostDao.insert(postDetail.post.toEntity(CommunityPostType.COMMENTED))
                }
            }
            .onFailure {
                if (it is NetworkResult.Failure.HttpError && it.code == NOT_FOUND) {
                    communityPostDao.deleteById(postId)
                }
            }
            .map { it.toModel() }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPosts(
        query: String,
        languageCode: String,
        postType: SimpleCommunityPostType,
    ): Flow<PagingData<SimpleCommunityPost>> {
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
                languageCode = languageCode,
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

    override suspend fun toggleBookmark(postId: Int): NetworkResult<Unit> =
        communityDataSource.toggleBookmark(postId = postId)

    override suspend fun toggleLike(postId: Int): NetworkResult<Unit> =
        communityDataSource.toggleLike(postId = postId)

    override suspend fun deletePost(postId: Int): NetworkResult<Unit> =
        communityDataSource.deletePost(postId = postId)
            .onSuccess { communityPostDao.deleteById(postId) }

    override suspend fun deleteComment(commentId: Int): NetworkResult<Unit> =
        communityDataSource.deleteComment(commentId = commentId)

    override suspend fun reportPost(postId: Int, reason: ReportReason): NetworkResult<Unit> =
        communityDataSource.reportPost(
            request = ReportCommunityPostRequest(
                postId = postId,
                reason = reason.name,
            ),
        )

    override suspend fun reportComment(commentId: Int, reason: ReportReason): NetworkResult<Unit> =
        communityDataSource.reportComment(
            request = ReportCommunityCommentRequest(
                commentId = commentId,
                reason = reason.name,
            ),
        )

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
    }
}
