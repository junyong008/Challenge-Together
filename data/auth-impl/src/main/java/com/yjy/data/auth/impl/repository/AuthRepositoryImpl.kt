package com.yjy.data.auth.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.onSuccess
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.network.datasource.AuthDataSource
import com.yjy.data.network.request.auth.ChangePasswordRequest
import com.yjy.data.network.request.auth.EmailLoginRequest
import com.yjy.data.network.request.auth.EmailRequest
import com.yjy.data.network.request.auth.SignUpRequest
import com.yjy.data.network.request.auth.VerifyRequest
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val sessionDataSource: SessionDataSource,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = sessionDataSource.isLoggedIn
    override val isSessionTokenAvailable: Flow<Boolean> = sessionDataSource.isSessionTokenAvailable

    override suspend fun getIsLoggedIn(): Boolean = sessionDataSource.getLoggedIn()

    override suspend fun logout() {
        sessionDataSource.setLoggedIn(false)
        sessionDataSource.setToken(null)
    }

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
    ): NetworkResult<Unit> = authDataSource.signUp(
        signUpRequest = SignUpRequest(
            nickname = nickname,
            email = email,
            password = hashPassword(password),
            kakaoId = kakaoId,
            googleId = googleId,
            naverId = naverId,
        ),
    ).onSuccess {
        sessionDataSource.setLoggedIn(true)
    }

    override suspend fun emailLogin(email: String, password: String): NetworkResult<Unit> =
        authDataSource.emailLogin(
            emailLoginRequest = EmailLoginRequest(
                email = email,
                password = hashPassword(password),
            ),
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        authDataSource.checkEmailDuplicate(email)

    override suspend fun requestVerifyCode(email: String): NetworkResult<Unit> =
        authDataSource.requestVerifyCode(
            emailRequest = EmailRequest(email = email),
        )

    override suspend fun verifyCode(email: String, verifyCode: String): NetworkResult<Unit> =
        authDataSource.verifyCode(
            verifyRequest = VerifyRequest(
                email = email,
                verifyCode = verifyCode,
            ),
        )

    override suspend fun changePassword(password: String): NetworkResult<Unit> =
        authDataSource.changePassword(
            changePasswordRequest = ChangePasswordRequest(password = hashPassword(password)),
        ).onSuccess {
            sessionDataSource.setLoggedIn(false)
            sessionDataSource.setToken(null)
        }

    override suspend fun deleteAccount(): NetworkResult<Unit> = authDataSource.deleteAccount()

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(bytes)
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}
