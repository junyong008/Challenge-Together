package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.common.network.onSuccess
import com.yjy.core.datastore.SessionDataSource
import com.yjy.core.datastore.UserPreferencesDataSource
import com.yjy.core.network.datasource.AuthDataSource
import java.security.MessageDigest
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val sessionDataSource: SessionDataSource,
    private val userPreferencesDataSource: UserPreferencesDataSource,
) : AuthRepository {

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
    ): NetworkResult<Unit> {
        return authDataSource.signUp(
            nickname = nickname,
            email = email,
            password = hashPassword(password),
            kakaoId = kakaoId,
            googleId = googleId,
            naverId = naverId,
        ).onSuccess {
            userPreferencesDataSource.setLoggedIn(true)
        }
    }

    override suspend fun emailLogin(email: String, password: String): NetworkResult<Unit> {
        return authDataSource.emailLogin(
            email = email,
            password = hashPassword(password),
        ).onSuccess {
            userPreferencesDataSource.setLoggedIn(true)
        }
    }

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        authDataSource.checkEmailDuplicate(email)

    override suspend fun requestVerifyCode(email: String): NetworkResult<Unit> =
        authDataSource.requestVerifyCode(email)

    override suspend fun verifyCode(email: String, verifyCode: String): NetworkResult<Unit> {
        return authDataSource.verifyCode(
            email = email,
            verifyCode = verifyCode,
        )
    }

    override suspend fun changePassword(password: String): NetworkResult<Unit> {
        return authDataSource.changePassword(hashPassword(password))
            .onSuccess {
                userPreferencesDataSource.setLoggedIn(false)
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
