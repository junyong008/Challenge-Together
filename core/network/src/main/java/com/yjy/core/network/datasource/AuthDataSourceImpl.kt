package com.yjy.core.network.datasource

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.request.ChangePasswordRequest
import com.yjy.core.network.request.EmailLoginRequest
import com.yjy.core.network.request.EmailRequest
import com.yjy.core.network.request.SignUpRequest
import com.yjy.core.network.request.VerifyRequest
import com.yjy.core.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : AuthDataSource {

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
    ): NetworkResult<Unit> {
        return challengeTogetherService.signUp(
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

    override suspend fun emailLogin(email: String, password: String): NetworkResult<Unit> {
        return challengeTogetherService.emailLogin(
            EmailLoginRequest(
                email = email,
                password = password,
            ),
        )
    }

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        challengeTogetherService.checkEmailDuplicate(email)

    override suspend fun requestVerifyCode(email: String): NetworkResult<Unit> =
        challengeTogetherService.requestVerifyCode(EmailRequest(email = email))

    override suspend fun verifyCode(email: String, verifyCode: String): NetworkResult<Unit> {
        return challengeTogetherService.verifyCode(
            VerifyRequest(
                email = email,
                verifyCode = verifyCode,
            ),
        )
    }

    override suspend fun changePassword(password: String): NetworkResult<Unit> =
        challengeTogetherService.changePassword(ChangePasswordRequest(password = password))
}
