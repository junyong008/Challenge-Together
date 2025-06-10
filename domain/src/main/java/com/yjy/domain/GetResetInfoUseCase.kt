package com.yjy.domain

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.ResetInfo
import com.yjy.model.challenge.ResetRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetResetInfoUseCase @Inject constructor(
    private val startedChallengeRepository: StartedChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int): Flow<NetworkResult<ResetInfo>> = combine(
        startedChallengeRepository.getResetInfo(challengeId),
        userRepository.timeDiff,
    ) { result, timeDiff ->
        result.map { resetInfo ->
            val updatedRecords = if (resetInfo.isCompleted || resetInfo.resetRecords.isEmpty()) {
                resetInfo.resetRecords.applyTimeDiff(timeDiff)
            } else {
                resetInfo.resetRecords
                    .applyTimeDiffExcludingLast(timeDiff)
                    .updateLastRecordInSeconds()
            }

            resetInfo.copy(resetRecords = updatedRecords)
        }
    }

    private fun List<ResetRecord>.applyTimeDiff(timeDiff: Long): List<ResetRecord> {
        return this.map { it.copy(resetDateTime = it.resetDateTime.plusSeconds(timeDiff)) }
    }

    private fun List<ResetRecord>.applyTimeDiffExcludingLast(timeDiff: Long): List<ResetRecord> {
        if (size <= 1) return this
        val updated = dropLast(1).map { it.copy(resetDateTime = it.resetDateTime.plusSeconds(timeDiff)) }
        return updated + last()
    }

    private fun List<ResetRecord>.updateLastRecordInSeconds(): List<ResetRecord> {
        if (this.size < 2) return this

        val updated = this.toMutableList()
        val secondLast = updated[updated.lastIndex - 1]
        val last = updated.last()

        updated[updated.lastIndex] = last.copy(
            recordInSeconds = ChronoUnit.SECONDS.between(
                secondLast.resetDateTime,
                last.resetDateTime,
            ),
        )

        return updated
    }
}
