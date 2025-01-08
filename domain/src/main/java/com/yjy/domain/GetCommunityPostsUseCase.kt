package com.yjy.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.data.community.api.CommunityRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.community.SimpleCommunityPost
import com.yjy.model.community.SimpleCommunityPostType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCommunityPostsUseCase @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(
        query: String = "",
        languageCode: String = "",
        postType: SimpleCommunityPostType,
    ): Flow<PagingData<SimpleCommunityPost>> =
        communityRepository.getPosts(query, languageCode, postType)
            .map { pagingData ->
                val timeDiff = userRepository.timeDiff.first()
                pagingData.map { post ->
                    post.applyTimeDiff(timeDiff)
                }
            }

    private fun SimpleCommunityPost.applyTimeDiff(timeDiff: Long): SimpleCommunityPost = copy(
        writtenDateTime = writtenDateTime.plusSeconds(timeDiff),
        modifiedDateTime = modifiedDateTime.plusSeconds(timeDiff),
    )
}
