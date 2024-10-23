package com.yjy.data.auth.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.onSuccess
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.network.datasource.AuthDataSource
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.EmailRequest
import com.yjy.data.network.request.SignUpRequest
import com.yjy.data.network.request.VerifyRequest
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
    ): NetworkResult<Unit> {
        return authDataSource.signUp(
            SignUpRequest(
                nickname = nickname,
                email = email,
                password = hashPassword(password),
                kakaoId = kakaoId,
                googleId = googleId,
                naverId = naverId,
            )
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }
    }

    override suspend fun emailLogin(email: String, password: String): NetworkResult<Unit> {
        return authDataSource.emailLogin(
            EmailLoginRequest(
                email = email,
                password = hashPassword(password),
            )
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }
    }

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        authDataSource.checkEmailDuplicate(email)

    override suspend fun requestVerifyCode(email: String): NetworkResult<Unit> {
        return authDataSource.requestVerifyCode(
            EmailRequest(email = email)
        )
    }

    override suspend fun verifyCode(email: String, verifyCode: String): NetworkResult<Unit> {
        return authDataSource.verifyCode(
            VerifyRequest(
                email = email,
                verifyCode = verifyCode,
            )
        )
    }

    override suspend fun changePassword(password: String): NetworkResult<Unit> {
        return authDataSource.changePassword(
            ChangePasswordRequest(password = hashPassword(password))
        ).onSuccess {
            sessionDataSource.setLoggedIn(false)
            sessionDataSource.setToken(null)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(bytes)
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}
