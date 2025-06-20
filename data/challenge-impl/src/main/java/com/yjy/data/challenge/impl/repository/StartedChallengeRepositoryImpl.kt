package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.fold
import com.yjy.common.network.map
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.data.challenge.impl.mapper.toDetailedStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toModel
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toSimpleStartedChallengeModel
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.datasource.StartedChallengeDataSource
import com.yjy.data.network.request.challenge.AddReasonToStartChallengeRequest
import com.yjy.data.network.request.challenge.ResetChallengeRequest
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.RecoveryProgress
import com.yjy.model.challenge.ResetInfo
import com.yjy.model.challenge.ResetRecord
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.StartReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

internal class StartedChallengeRepositoryImpl @Inject constructor(
    private val startedChallengeDataSource: StartedChallengeDataSource,
    private val challengeDao: ChallengeDao,
    timeManager: TimeManager,
) : StartedChallengeRepository {

    private val challenges: Flow<List<ChallengeEntity>> = challengeDao.getAll()
    private val tickerFlow: Flow<LocalDateTime> = timeManager.tickerFlow

    override val startedChallenges: Flow<List<SimpleStartedChallenge>> = challenges
        .map { entities ->
            entities
                .filter { it.isStarted && !it.isCompleted }
                .map { it.toSimpleStartedChallengeModel() }
        }
        .combine(tickerFlow) { challenges, currentTime ->
            challenges.map { challenge -> challenge.updateCurrentRecord(currentTime) }
        }

    private fun SimpleStartedChallenge.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime),
    )

    override val completedChallenges: Flow<List<SimpleStartedChallenge>> = challenges
        .map { entities ->
            entities
                .filter { it.isStarted && it.isCompleted }
                .map { it.toSimpleStartedChallengeModel() }
        }

    override suspend fun resetStartedChallenge(
        challengeId: Int,
        resetDateTime: LocalDateTime,
        memo: String,
    ): NetworkResult<Unit> =
        startedChallengeDataSource.resetStartedChallenge(
            request = ResetChallengeRequest(
                challengeId = challengeId,
                resetDateTime = resetDateTime.toRequestString(),
                resetMemo = memo,
            ),
        )

    override suspend fun addReasonToStartChallenge(challengeId: Int, reason: String): NetworkResult<Unit> =
        startedChallengeDataSource.addReasonToStartChallenge(
            request = AddReasonToStartChallengeRequest(
                challengeId = challengeId,
                reason = reason,
            ),
        )

    override suspend fun deleteReasonToStartChallenge(reasonId: Int): NetworkResult<Unit> =
        startedChallengeDataSource.deleteReasonToStartChallenge(reasonId)

    override suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit> =
        startedChallengeDataSource.deleteStartedChallenge(challengeId)
            .onSuccess { challengeDao.deleteById(challengeId) }

    override suspend fun continueStartedChallenge(challengeId: Int): NetworkResult<Unit> =
        startedChallengeDataSource.continueStartedChallenge(challengeId)

    override suspend fun forceRemoveStartedChallengeMember(memberId: Int): NetworkResult<Unit> =
        startedChallengeDataSource.forceRemoveFromStartedChallenge(memberId)

    override suspend fun getStartedChallengeDetail(
        challengeId: Int,
    ): Flow<NetworkResult<DetailedStartedChallenge>> =
        startedChallengeDataSource.getStartedChallengeDetail(challengeId).fold(
            onSuccess = { result ->
                val challenge = result.toDetailedStartedChallengeModel()
                challengeDao.insert(result.toEntity())

                tickerFlow.map { currentTime ->
                    NetworkResult.Success(challenge.updateCurrentRecord(currentTime))
                }
            },
            onFailure = { flowOf(it) },
        )

    private fun DetailedStartedChallenge.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime),
    )

    override suspend fun getResetInfo(challengeId: Int): Flow<NetworkResult<ResetInfo>> {
        val resetInfoResponse = startedChallengeDataSource.getResetInfo(challengeId)

        return tickerFlow.map { currentTime ->
            val currentItem = ResetRecord(
                id = Int.MAX_VALUE,
                resetDateTime = currentTime,
                recordInSeconds = 0,
                content = "",
                isCurrent = true,
            )

            resetInfoResponse.map { result ->
                val resetInfo = result.toModel()
                if (!resetInfo.isCompleted && resetInfo.resetRecords.isNotEmpty()) {
                    val updatedRecords = (resetInfo.resetRecords + currentItem).sortedBy { it.id }
                    resetInfo.copy(resetRecords = updatedRecords)
                } else {
                    resetInfo
                }
            }
        }
    }

    override suspend fun getStartReasons(challengeId: Int): NetworkResult<List<StartReason>> =
        startedChallengeDataSource.getStartReasons(challengeId).map { reasons -> reasons.map { it.toModel() } }

    override suspend fun getChallengeRanking(challengeId: Int): Flow<NetworkResult<List<ChallengeRank>>> =
        startedChallengeDataSource.getChallengeRanking(challengeId).fold(
            onSuccess = { result ->
                val challengeRanks = result.map { it.toModel() }
                tickerFlow.map { currentTime ->
                    NetworkResult.Success(challengeRanks.map { it.updateCurrentRecord(currentTime) })
                }
            },
            onFailure = { flowOf(it) },
        )

    override suspend fun getChallengeProgress(challengeId: Int): NetworkResult<RecoveryProgress> =
        startedChallengeDataSource.getChallengeProgress(challengeId).map { it.toModel() }

    private fun ChallengeRank.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime),
    )
}
