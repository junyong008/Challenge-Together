package com.yjy.data.community.impl.mapper

import com.yjy.data.database.model.CommunityPostEntity
import com.yjy.data.database.model.CommunityPostType
import com.yjy.data.network.response.community.CommentResponse
import com.yjy.data.network.response.community.GetPostResponse
import com.yjy.data.network.response.community.GetPostsResponse
import com.yjy.data.network.response.community.PostResponse
import com.yjy.model.common.Tier
import com.yjy.model.community.CommunityComment
import com.yjy.model.community.CommunityPost
import com.yjy.model.community.DetailedCommunityPost
import com.yjy.model.community.SimpleCommunityPost

internal fun GetPostsResponse.toEntity() = CommunityPostEntity(
    id = postId,
    content = content,
    writerName = author.name,
    writerBestRecordInSeconds = author.bestRecordInSeconds,
    commentCount = commentCount,
    likeCount = likeCount,
    type = type.toCommunityPostType(),
    writtenDateTime = writtenDateTime.toLocalDateTime(),
    modifiedDateTime = modifiedDateTime.toLocalDateTime(),
)

internal fun CommunityPostEntity.toModel() = SimpleCommunityPost(
    postId = id,
    writer = UserMapper.create(
        name = writerName,
        tier = Tier.getCurrentTier(writerBestRecordInSeconds),
    ),
    content = content,
    commentCount = commentCount,
    likeCount = likeCount,
    writtenDateTime = writtenDateTime,
    modifiedDateTime = modifiedDateTime,
)

internal fun PostResponse.toEntity(type: CommunityPostType) = CommunityPostEntity(
    id = postId,
    content = content,
    writerName = author.name,
    writerBestRecordInSeconds = author.bestRecordInSeconds,
    commentCount = commentCount,
    likeCount = likeCount,
    type = type,
    writtenDateTime = writtenDateTime.toLocalDateTime(),
    modifiedDateTime = modifiedDateTime.toLocalDateTime(),
)

internal fun GetPostResponse.toModel() = DetailedCommunityPost(
    post = post.toModel(),
    comments = comments.map { it.toModel() },
)

private fun PostResponse.toModel() = CommunityPost(
    postId = postId,
    writer = UserMapper.create(
        name = author.name,
        tier = Tier.getCurrentTier(author.bestRecordInSeconds),
    ),
    content = content,
    commentCount = commentCount,
    likeCount = likeCount,
    writtenDateTime = writtenDateTime.toLocalDateTime(),
    modifiedDateTime = modifiedDateTime.toLocalDateTime(),
    isAuthor = isAuthor,
    isLiked = isLiked,
    isBookmarked = isBookmarked,
)

private fun CommentResponse.toModel() = CommunityComment(
    commentId = commentId,
    parentCommentId = parentCommentId,
    writer = UserMapper.create(
        name = author.name,
        tier = Tier.getCurrentTier(author.bestRecordInSeconds),
    ),
    content = content,
    writtenDateTime = writtenDateTime.toLocalDateTime(),
    isAuthor = isAuthor,
    isPostWriter = isPostWriter,
)
