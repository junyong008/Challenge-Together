package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.Profile
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.TargetDays
import com.yjy.model.challenge.WaitingChallenge
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ChallengeRepository {

    val recentCompletedChallengeTitles: Flow<List<String>>
    val startedChallenges: Flow<List<StartedChallenge>>
    val waitingChallenges: Flow<List<WaitingChallenge>>
    val profile: Flow<Profile>

    suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime? = null,
        maxParticipants: Int = 1,
        roomPassword: String = "",
    ): NetworkResult<String>

    suspend fun clearRecentCompletedChallenges()
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun syncProfile(): NetworkResult<Unit>
    suspend fun syncChallenges(): NetworkResult<Unit>
}