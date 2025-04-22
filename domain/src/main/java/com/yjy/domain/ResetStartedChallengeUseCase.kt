package com.yjy.domain

import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.data.user.api.UserRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class ResetStartedChallengeUseCase @Inject constructor(
    private val startedChallengeRepository: StartedChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int, resetDateTime: LocalDateTime, memo: String): NetworkResult<Unit> {
        return startedChallengeRepository.resetStartedChallenge(
            challengeId = challengeId,
            resetDateTime = resetDateTime.minusSeconds(userRepository.timeDiff.first()),
            memo = memo,
        )
    }
}
