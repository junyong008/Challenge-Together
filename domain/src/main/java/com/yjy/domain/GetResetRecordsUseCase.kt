package com.yjy.domain

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.ResetRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetResetRecordsUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(challengeId: Int): Flow<NetworkResult<List<ResetRecord>>> = combine(
        flowOf(challengeRepository.getResetRecords(challengeId)),
        userRepository.timeDiff,
    ) { result, timeDiff ->
        result.map { records ->
            records.map { it.applyTimeDiff(timeDiff) }
        }
    }

    private fun ResetRecord.applyTimeDiff(timeDiff: Long): ResetRecord =
        copy(resetDateTime = resetDateTime.plusSeconds(timeDiff))
}
