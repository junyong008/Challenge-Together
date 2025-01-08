package com.yjy.data.network.service

import com.yjy.data.network.request.challenge.AddChallengePostRequest
import com.yjy.data.network.response.challenge.ChallengePostResponse
import kotlinx.coroutines.flow.Flow

interface ChallengePostWebSocketService {
    fun connectAsFlow(challengeId: Int): Flow<ChallengePostResponse>
    fun addPost(request: AddChallengePostRequest)
}
