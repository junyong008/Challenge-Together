package com.yjy.data.challenge.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.TargetDays
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
    ): NetworkResult<String>
}