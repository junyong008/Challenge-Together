package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.isFailure
import com.yjy.common.network.map
import com.yjy.common.network.mapToUnit
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toWaitingChallengeModel
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.model.Profile
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
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val challengeDataSource: ChallengeDataSource,
    private val challengeDao: ChallengeDao,
) : ChallengeRepository {

    private val timeDiff: Flow<Long> = userPreferencesDataSource.timeDiff
    private val challenges: Flow<List<ChallengeEntity>> = challengeDao.getAll()

    override val recentCompletedChallengeTitles: Flow<List<String>> =
        userPreferencesDataSource.completedChallengeTitles

    override val startedChallenges: Flow<List<StartedChallenge>> = combine(
        challenges,
        timeDiff,
    ) { challenges, timeDiff ->
        challenges
            .filter { it.isStarted && it.recentResetDateTime != null && !it.isCompleted }
            .map { it.toStartedChallengeModel(timeDiff) }
    }

    override val waitingChallenges: Flow<List<WaitingChallenge>> = challenges.map { challenges ->
        challenges
            .filter { !it.isStarted && it.recentResetDateTime == null }
            .map { it.toWaitingChallengeModel() }
    }

    override val profile: Flow<Profile> = combine(
        userPreferencesDataSource.userName,
        userPreferencesDataSource.unViewedNotificationCount,
    ) { name, count ->
        Profile(name, count)
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
        userPreferencesDataSource.setCompletedChallengeTitles(emptyList())

    override suspend fun syncTime(): NetworkResult<Unit> = challengeDataSource.syncTime()

    override suspend fun syncProfile(): NetworkResult<Unit> {
        return challengeDataSource.getProfile()
            .mapToUnit { profile ->
                userPreferencesDataSource.setUserName(profile.userName)
                userPreferencesDataSource.setUnViewedNotificationCount(profile.unViewedNotificationCount)
            }
    }

    override suspend fun syncChallenges(): NetworkResult<Unit> {
        val getRecentCompletedChallengesTitle = challengeDataSource.getCompleteChallengesTitle()
            .map { titles -> titles.map { it.title } }
            .mapToUnit { titles ->
                userPreferencesDataSource.addCompletedChallengeTitles(titles)
            }
        if (getRecentCompletedChallengesTitle.isFailure) return getRecentCompletedChallengesTitle

        return challengeDataSource.getMyChallenges()
            .map { it.map(GetMyChallengesResponse::toEntity) }
            .mapToUnit { entities ->
                challengeDao.syncChallenges(entities)
            }
    }
}