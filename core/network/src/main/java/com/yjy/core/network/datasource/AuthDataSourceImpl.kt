package com.yjy.core.network.datasource

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : AuthDataSource {

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        challengeTogetherService.checkEmailDuplicate(email)
}