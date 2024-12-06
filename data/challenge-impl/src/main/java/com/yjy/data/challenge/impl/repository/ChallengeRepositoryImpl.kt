package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.challenge.AddChallengeRequest
import com.yjy.data.network.request.challenge.EditChallengeTitleDescriptionRequest
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime
import javax.inject.Inject

internal class ChallengeRepositoryImpl @Inject constructor(
    private val challengePreferencesDataSource: ChallengePreferencesDataSource,
    private val challengeDataSource: ChallengeDataSource,
    private val challengeDao: ChallengeDao,
) : ChallengeRepository {

    override suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime?,
        maxParticipants: Int,
        roomPassword: String,
    ): NetworkResult<Int> = challengeDataSource.addChallenge(
        request = AddChallengeRequest(
            category = category.toRequestString(),
            title = title,
            description = description,
            targetDays = targetDays.toRequestString(),
            startDateTime = startDateTime?.toRequestString() ?: "",
            maxParticipants = maxParticipants.toString(),
            password = roomPassword,
        ),
    ).map { it.challengeId }

    override suspend fun editChallengeTitleDescription(
        challengeId: Int,
        title: String,
        description: String,
    ): NetworkResult<Unit> = challengeDataSource.editChallengeTitleDescription(
        request = EditChallengeTitleDescriptionRequest(
            challengeId = challengeId,
            title = title,
            description = description,
        ),
    )

    override suspend fun editChallengeCategory(
        challengeId: Int,
        category: Category,
    ): NetworkResult<Unit> = challengeDataSource.editChallengeCategory(
        challengeId = challengeId,
        category = category.toRequestString(),
    )

    override suspend fun editChallengeTargetDays(
        challengeId: Int,
        targetDays: TargetDays,
    ): NetworkResult<Unit> = challengeDataSource.editChallengeTargetDays(
        challengeId = challengeId,
        targetDays = targetDays.toRequestString(),
    )

    override suspend fun syncChallenges(): NetworkResult<List<String>> = challengeDataSource.getMyChallenges()
        .onSuccess { response ->
            val challenges = response.challenges.map { it.toEntity() }
            challengeDao.syncChallenges(challenges)
            challengePreferencesDataSource.addCompletedChallengeTitles(response.newlyCompletedTitles)
        }
        .map { response ->
            response.newlyCompletedTitles
        }

    override suspend fun clearLocalData() {
        challengeDao.deleteAll()
    }
}
