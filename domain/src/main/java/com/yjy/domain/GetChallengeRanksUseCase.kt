package com.yjy.domain

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.challenge.core.TargetDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetChallengeRanksUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int): Flow<NetworkResult<List<ChallengeRank>>> =
        combine(
            challengeRepository.getChallengeRanking(challengeId),
            userRepository.timeDiff,
        ) { result, timeDiff ->
            result.map { challengeRanks ->
                challengeRanks.map { challengeRank ->
                    challengeRank
                        .applyTimeDiff(timeDiff)
                        .applyTargetDaysLimit()
                }
            }
        }

    private fun ChallengeRank.applyTimeDiff(timeDiff: Long): ChallengeRank {
        return copy(
            recentResetDateTime = recentResetDateTime.plusSeconds(timeDiff),
            currentRecordInSeconds = currentRecordInSeconds.minus(timeDiff),
        )
    }

    private fun ChallengeRank.applyTargetDaysLimit(): ChallengeRank {
        return when (val targetDays = targetDays) {
            is TargetDays.Fixed -> {
                val targetSeconds = targetDays.days * SECONDS_PER_DAY
                val isReachedTarget = currentRecordInSeconds >= targetSeconds

                copy(
                    currentRecordInSeconds = currentRecordInSeconds.coerceAtMost(targetSeconds),
                    isComplete = isReachedTarget,
                    isInActive = if (isReachedTarget) false else isInActive,
                )
            }
            TargetDays.Infinite -> this
        }
    }
}
