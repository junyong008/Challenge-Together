package com.yjy.core.network.datasource

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.request.SignUpRequest
import com.yjy.core.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : AuthDataSource {

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        challengeTogetherService.checkEmailDuplicate(email)

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
    ): NetworkResult<Unit> = challengeTogetherService.signUp(
        SignUpRequest(
            nickname = nickname,
            email = email,
            password = password,
            kakaoId = kakaoId,
            googleId = googleId,
            naverId = naverId,
        ),
    )
}
