package com.yjy.domain

import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.SimpleStartedChallenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetStartedChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<SimpleStartedChallenge>> = combine(
        challengeRepository.startedChallenges,
        userRepository.timeDiff,
    ) { challenges, timeDiff ->
        challenges.map { it.applyTimeDiff(timeDiff) }
    }

    private fun SimpleStartedChallenge.applyTimeDiff(timeDiff: Long): SimpleStartedChallenge {
        return copy(
            recentResetDateTime = recentResetDateTime.plusSeconds(timeDiff),
            currentRecordInSeconds = currentRecordInSeconds.minus(timeDiff),
        )
    }
}
