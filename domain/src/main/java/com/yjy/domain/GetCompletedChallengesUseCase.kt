package com.yjy.domain

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.TargetDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCompletedChallengesUseCase @Inject constructor(
    private val startedChallengeRepository: StartedChallengeRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<List<SimpleStartedChallenge>> = combine(
        startedChallengeRepository.completedChallenges,
        userRepository.timeDiff,
    ) { challenges, timeDiff ->
        challenges.map { challenge ->
            challenge
                .applyTimeDiff(timeDiff)
                .applyTargetDaysLimit()
        }
    }

    private fun SimpleStartedChallenge.applyTimeDiff(timeDiff: Long): SimpleStartedChallenge {
        return copy(
            recentResetDateTime = recentResetDateTime.plusSeconds(timeDiff),
            currentRecordInSeconds = currentRecordInSeconds.minus(timeDiff),
        )
    }

    private fun SimpleStartedChallenge.applyTargetDaysLimit(): SimpleStartedChallenge {
        val limitedRecord = when (val targetDays = targetDays) {
            is TargetDays.Fixed -> currentRecordInSeconds.coerceAtMost(
                targetDays.days * SECONDS_PER_DAY,
            )
            TargetDays.Infinite -> currentRecordInSeconds
        }
        return copy(currentRecordInSeconds = limitedRecord)
    }
}
