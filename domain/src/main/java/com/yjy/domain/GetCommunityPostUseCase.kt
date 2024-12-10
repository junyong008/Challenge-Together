package com.yjy.domain

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.community.api.CommunityRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.community.CommunityComment
import com.yjy.model.community.CommunityPost
import com.yjy.model.community.DetailedCommunityPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetCommunityPostUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(postId: Int): Flow<NetworkResult<DetailedCommunityPost>> = combine(
        flowOf(communityRepository.getPost(postId)),
        userRepository.timeDiff,
    ) { result, timeDiff ->
        result.map { postDetail ->
            postDetail
                .applyTimeDiff(timeDiff)
                .sortComments()
        }
    }

    private fun DetailedCommunityPost.sortComments(): DetailedCommunityPost = copy(
        comments = comments.sortedBy { it.writtenDateTime },
    )

    private fun DetailedCommunityPost.applyTimeDiff(timeDiff: Long): DetailedCommunityPost = copy(
        post = post.applyTimeDiff(timeDiff),
        comments = comments.map { it.applyTimeDiff(timeDiff) },
    )

    private fun CommunityPost.applyTimeDiff(timeDiff: Long): CommunityPost = copy(
        writtenDateTime = writtenDateTime.plusSeconds(timeDiff),
        modifiedDateTime = modifiedDateTime.plusSeconds(timeDiff),
    )

    private fun CommunityComment.applyTimeDiff(timeDiff: Long): CommunityComment = copy(
        writtenDateTime = writtenDateTime.plusSeconds(timeDiff),
    )
}
