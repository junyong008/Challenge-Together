package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.response.AddChallengeResponse

interface ChallengeDataSource {

    suspend fun addChallenge(
        category: String,
        title: String,
        description: String,
        startDateTime: String,
        targetDays: String,
        maxParticipants: String,
        password: String,
    ): NetworkResult<AddChallengeResponse>
}