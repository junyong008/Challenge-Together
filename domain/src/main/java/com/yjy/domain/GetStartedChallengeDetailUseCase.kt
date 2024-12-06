package com.yjy.domain

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.core.TargetDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetStartedChallengeDetailUseCase @Inject constructor(
    private val startedChallengeRepository: StartedChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int): Flow<NetworkResult<DetailedStartedChallenge>> =
        combine(
            startedChallengeRepository.getStartedChallengeDetail(challengeId),
            userRepository.timeDiff,
        ) { result, timeDiff ->
            result.map { challenge ->
                challenge
                    .applyTimeDiff(timeDiff)
                    .applyTargetDaysLimit()
            }
        }

    private fun DetailedStartedChallenge.applyTimeDiff(timeDiff: Long): DetailedStartedChallenge {
        return copy(
            recentResetDateTime = recentResetDateTime.plusSeconds(timeDiff),
            startDateTime = startDateTime.plusSeconds(timeDiff),
            currentRecordInSeconds = currentRecordInSeconds.minus(timeDiff),
        )
    }

    private fun DetailedStartedChallenge.applyTargetDaysLimit(): DetailedStartedChallenge {
        val limitedRecord = when (val targetDays = targetDays) {
            is TargetDays.Fixed -> currentRecordInSeconds.coerceAtMost(
                targetDays.days * SECONDS_PER_DAY,
            )
            TargetDays.Infinite -> currentRecordInSeconds
        }
        return copy(currentRecordInSeconds = limitedRecord)
    }
}
