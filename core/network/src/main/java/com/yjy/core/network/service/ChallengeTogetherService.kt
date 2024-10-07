package com.yjy.core.network.service

import com.yjy.core.common.network.NetworkResult
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ChallengeTogetherService {

    @GET("auth/check-email-duplicate")
    suspend fun checkEmailDuplicate(
        @Query("email") email: String,
    ): NetworkResult<Unit>
}