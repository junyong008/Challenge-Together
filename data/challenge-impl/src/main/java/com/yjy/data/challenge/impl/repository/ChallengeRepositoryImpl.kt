package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.TargetDays
import java.time.LocalDateTime
import javax.inject.Inject

internal class ChallengeRepositoryImpl @Inject constructor(
    private val challengeDataSource: ChallengeDataSource,
) : ChallengeRepository {

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
}