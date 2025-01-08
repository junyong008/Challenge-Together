package com.yjy.domain

import com.yjy.data.challenge.api.ChallengePostRepository
import com.yjy.data.user.api.UserRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class AddChallengePostUseCase @Inject constructor(
    private val challengePostRepository: ChallengePostRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int, content: String) {
        challengePostRepository.addChallengePost(
            challengeId = challengeId,
            content = content,
            tempWrittenDateTime = LocalDateTime.now().minusSeconds(userRepository.timeDiff.first()),
        )
    }
}
