package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime

interface ChallengeRepository {
    suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime? = null,
        maxParticipants: Int = 1,
        roomPassword: String = "",
    ): NetworkResult<Int>
    suspend fun editChallengeTitleDescription(challengeId: Int, title: String, description: String): NetworkResult<Unit>
    suspend fun editChallengeCategory(challengeId: Int, category: Category): NetworkResult<Unit>
    suspend fun editChallengeTargetDays(challengeId: Int, targetDays: TargetDays): NetworkResult<Unit>
    suspend fun syncChallenges(): NetworkResult<List<String>>
    suspend fun clearLocalData()
}
