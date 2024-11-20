package com.yjy.domain

import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.ChallengePost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChallengePostsUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(challengeId: Int): Flow<PagingData<ChallengePost>> =
        challengeRepository.getChallengePosts(challengeId)
            .map { pagingData ->
                pagingData.map { post ->
                    val timeDiff = userRepository.timeDiff.first()
                    post.applyTimeDiff(timeDiff)
                }
            }

    private fun ChallengePost.applyTimeDiff(timeDiff: Long): ChallengePost =
        copy(writtenDateTime = writtenDateTime.plusSeconds(timeDiff))
}
