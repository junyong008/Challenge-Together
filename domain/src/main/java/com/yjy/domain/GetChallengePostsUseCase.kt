package com.yjy.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.data.challenge.api.ChallengePostRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.ChallengePost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChallengePostsUseCase @Inject constructor(
    private val challengePostRepository: ChallengePostRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(challengeId: Int): Flow<PagingData<ChallengePost>> =
        challengePostRepository.getChallengePosts(challengeId)
            .map { pagingData ->
                pagingData.map { post ->
                    val timeDiff = userRepository.timeDiff.first()
                    post.applyTimeDiff(timeDiff)
                }
            }

    private fun ChallengePost.applyTimeDiff(timeDiff: Long): ChallengePost =
        copy(writtenDateTime = writtenDateTime.plusSeconds(timeDiff))
}
