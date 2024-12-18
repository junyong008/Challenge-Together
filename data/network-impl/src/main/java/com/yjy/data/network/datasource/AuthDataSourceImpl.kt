package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.auth.ChangePasswordRequest
import com.yjy.data.network.request.auth.EmailLoginRequest
import com.yjy.data.network.request.auth.EmailRequest
import com.yjy.data.network.request.auth.SignUpRequest
import com.yjy.data.network.request.auth.VerifyRequest
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class AuthDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : AuthDataSource {

    override suspend fun signUp(signUpRequest: SignUpRequest): NetworkResult<Unit> =
        challengeTogetherService.signUp(signUpRequest)

    override suspend fun emailLogin(emailLoginRequest: EmailLoginRequest): NetworkResult<Unit> =
        challengeTogetherService.emailLogin(emailLoginRequest)

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        challengeTogetherService.checkEmailDuplicate(email)

    override suspend fun requestVerifyCode(emailRequest: EmailRequest): NetworkResult<Unit> =
        challengeTogetherService.requestVerifyCode(emailRequest)

    override suspend fun verifyCode(verifyRequest: VerifyRequest): NetworkResult<Unit> =
        challengeTogetherService.verifyCode(verifyRequest)

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): NetworkResult<Unit> =
        challengeTogetherService.changePassword(changePasswordRequest)

    override suspend fun deleteAccount(): NetworkResult<Unit> =
        challengeTogetherService.deleteAccount()
}
