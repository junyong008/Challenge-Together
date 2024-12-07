package com.yjy.data.community.impl.mapper

import com.yjy.data.database.model.CommunityPostEntity
import com.yjy.data.network.response.community.GetPostsResponse
import com.yjy.model.common.Tier
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
