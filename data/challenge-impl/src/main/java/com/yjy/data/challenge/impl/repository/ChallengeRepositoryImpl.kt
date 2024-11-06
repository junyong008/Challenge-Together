package com.yjy.data.challenge.impl.repository

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.mapper.toDetailedStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toModel
import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toSimpleStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toSimpleWaitingChallengeModel
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

internal class ChallengeRepositoryImpl @Inject constructor(
    private val challengePreferencesDataSource: ChallengePreferencesDataSource,
    private val challengeDataSource: ChallengeDataSource,
    private val challengeDao: ChallengeDao,
    timeManager: TimeManager,
) : ChallengeRepository {

    private val tickerFlow: Flow<LocalDateTime> = timeManager.tickerFlow
    private val challenges: Flow<List<ChallengeEntity>> = challengeDao.getAll()

    override val timeChangedFlow: Flow<Unit> = timeManager.timeChangedFlow

    override val currentTier: Flow<Tier> =
        challengePreferencesDataSource.currentTier.map { it.toModel() }

    override val sortOrder: Flow<SortOrder> =
        challengePreferencesDataSource.sortOrder.map { it.toModel() }

    override val recentCompletedChallengeTitles: Flow<List<String>> =
        challengePreferencesDataSource.completedChallengeTitles

    override val startedChallenges: Flow<List<SimpleStartedChallenge>> = challenges
        .map { entities ->
            entities
                .filter { it.isStarted }
                .map { it.toSimpleStartedChallengeModel() }
        }
        .combine(tickerFlow) { challenges, currentTime ->
            challenges.map { challenge -> challenge.updateCurrentRecord(currentTime) }
        }
        .combine(sortOrder) { challenges, order ->
            challenges.sortBy(order)
        }

    private fun SimpleStartedChallenge.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = calculateCurrentRecord(
            recentResetDateTime = recentResetDateTime,
            currentTime = currentTime,
            targetDays = targetDays,
        )
    )

    private fun calculateCurrentRecord(
        recentResetDateTime: LocalDateTime,
        currentTime: LocalDateTime,
        targetDays: TargetDays,
    ): Long {
        val currentRecord = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime)
        return when (targetDays) {
            is TargetDays.Fixed -> currentRecord.coerceAtMost(targetDays.days * SECONDS_PER_DAY)
            TargetDays.Infinite -> currentRecord
        }
    }

    private fun List<SimpleStartedChallenge>.sortBy(sortOrder: SortOrder): List<SimpleStartedChallenge> {
        return when (sortOrder) {
            SortOrder.LATEST -> this.sortedByDescending { it.id.toInt() }
            SortOrder.OLDEST -> this.sortedBy { it.id.toInt() }
            SortOrder.TITLE -> this.sortedBy { it.title }
            SortOrder.HIGHEST_RECORD -> this.sortedByDescending { it.currentRecordInSeconds }
            SortOrder.LOWEST_RECORD -> this.sortedBy { it.currentRecordInSeconds }
        }
    }

    override val waitingChallenges: Flow<List<SimpleWaitingChallenge>> = challenges.map { challenges ->
        challenges
            .filter { !it.isStarted }
            .map { it.toSimpleWaitingChallengeModel() }
    }

    override suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime?,
        maxParticipants: Int,
        roomPassword: String,
    ): NetworkResult<String> {
        return challengeDataSource.addChallenge(
            AddChallengeRequest(
                category = category.toRequestString(),
                title = title,
                description = description,
                targetDays = targetDays.toRequestString(),
                startDateTime = startDateTime?.toRequestString() ?: "",
                maxParticipants = maxParticipants.toString(),
                password = roomPassword,
            ),
        ).map { it.challengeId }
    }

    override suspend fun getStartedChallengeDetail(
        challengeId: String,
    ): Flow<NetworkResult<DetailedStartedChallenge>> = when (
        val result = challengeDataSource.getStartedChallengeDetail(challengeId)
    ) {
        is NetworkResult.Success -> {
            val challenge = result.data.toDetailedStartedChallengeModel()
            tickerFlow.map { currentTime ->
                NetworkResult.Success(challenge.updateCurrentRecord(currentTime))
            }
        }
        is NetworkResult.Failure -> flowOf(result)
    }

    private fun DetailedStartedChallenge.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = calculateCurrentRecord(
            recentResetDateTime = recentResetDateTime,
            currentTime = currentTime,
            targetDays = targetDays,
        )
    )

    override suspend fun setCurrentTier(tier: Tier) =
        challengePreferencesDataSource.setCurrentTier(tier.toProto())

    override suspend fun setSortOrder(order: SortOrder) =
        challengePreferencesDataSource.setSortOrder(order.toProto())

    override suspend fun clearRecentCompletedChallenges() =
        challengePreferencesDataSource.setCompletedChallengeTitles(emptyList())

    override suspend fun syncChallenges(): NetworkResult<List<String>> {
        return challengeDataSource.getMyChallenges()
            .onSuccess { response ->
                val challenges = response.challenges.map { it.toEntity() }
                challengeDao.syncChallenges(challenges)
                challengePreferencesDataSource.addCompletedChallengeTitles(response.newlyCompletedTitles)
            }
            .map { response ->
                response.newlyCompletedTitles
            }
    }
}
