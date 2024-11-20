package com.yjy.domain

import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class AddChallengePostUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int, content: String) {
        challengeRepository.addChallengePost(
            challengeId = challengeId,
            content = content,
            tempWrittenDateTime = LocalDateTime.now().minusSeconds(userRepository.timeDiff.first()),
        )
    }
}
