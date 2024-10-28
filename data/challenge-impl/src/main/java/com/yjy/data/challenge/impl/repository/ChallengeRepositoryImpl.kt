package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toWaitingChallengeModel
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.TargetDays
import com.yjy.model.challenge.WaitingChallenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

internal class ChallengeRepositoryImpl @Inject constructor(
    private val challengePreferencesDataSource: ChallengePreferencesDataSource,
    private val challengeDataSource: ChallengeDataSource,
    private val challengeDao: ChallengeDao,
) : ChallengeRepository {

    private val timeDiff: Flow<Long> = challengePreferencesDataSource.timeDiff
    private val challenges: Flow<List<ChallengeEntity>> = challengeDao.getAll()

    override val recentCompletedChallengeTitles: Flow<List<String>> =
        challengePreferencesDataSource.completedChallengeTitles

    override val startedChallenges: Flow<List<StartedChallenge>> = combine(
        challenges,
        timeDiff,
    ) { challenges, timeDiff ->
        challenges
            .filter { it.isStarted && it.recentResetDateTime != null }
            .map { it.toStartedChallengeModel(timeDiff) }
    }

    override val waitingChallenges: Flow<List<WaitingChallenge>> = challenges.map { challenges ->
        challenges
            .filter { !it.isStarted && it.recentResetDateTime == null }
            .map { it.toWaitingChallengeModel() }
    }

    override suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime?,
        maxParticipants: Int,
        roomPassword: String
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
            )
        ).map { it.challengeId }
    }

    override suspend fun clearRecentCompletedChallenges() =
        challengePreferencesDataSource.setCompletedChallengeTitles(emptyList())

    override suspend fun syncTime(): NetworkResult<Unit> = challengeDataSource.syncTime()

    override suspend fun syncChallenges(): NetworkResult<List<String>> {
        return challengeDataSource.getMyChallenges()
            .map { response ->
                val challenges = response.challenges.map { it.toEntity() }
                val newlyCompletedTitles = response.newlyCompletedTitles

                challengeDao.syncChallenges(challenges)
                challengePreferencesDataSource.addCompletedChallengeTitles(newlyCompletedTitles)

                response.newlyCompletedTitles
            }
    }
}