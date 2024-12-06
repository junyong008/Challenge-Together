package com.yjy.data.challenge.impl.mapper

import com.yjy.data.database.model.ChallengePostEntity
import com.yjy.data.network.response.challenge.ChallengePostResponse
import com.yjy.data.network.response.challenge.GetChallengePostsResponse
import com.yjy.model.challenge.ChallengePost
import com.yjy.model.common.Tier

internal fun GetChallengePostsResponse.toEntity(challengeId: Int) = ChallengePostEntity(
    id = postId,
    tempId = postId,
    challengeId = challengeId,
    writerName = writerName,
    writerBestRecordInSeconds = writerBestRecordInSeconds,
    content = content,
    writtenDateTime = writtenDateTime.toLocalDateTime(),
    isAuthor = isAuthor,
    isSynced = true,
)

internal fun ChallengePostEntity.toModel() = ChallengePost(
    postId = id,
    writer = UserMapper.create(
        name = writerName,
        tier = Tier.getCurrentTier(writerBestRecordInSeconds),
    ),
    content = content,
    writtenDateTime = writtenDateTime,
    isAuthor = isAuthor,
    isSynced = isSynced,
)

internal fun ChallengePostResponse.toEntity() = ChallengePostEntity(
    id = postId,
    tempId = tempId,
    challengeId = challengeId,
    writerName = writerName,
    writerBestRecordInSeconds = writerBestRecordInSeconds,
    content = content,
    writtenDateTime = writtenDateTime.toLocalDateTime(),
    isAuthor = isAuthor,
    isSynced = true,
)
