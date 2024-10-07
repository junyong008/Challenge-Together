package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.datasource.AuthDataSource
import java.security.MessageDigest
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        authDataSource.checkEmailDuplicate(email)

    override suspend fun signUp(
        nickname: String,
        email: String,
        password: String,
        kakaoId: String,
        googleId: String,
        naverId: String,
    ): NetworkResult<Unit> = authDataSource.signUp(
        nickname = nickname,
        email = email,
        password = hashPassword(password),
        kakaoId = kakaoId,
        googleId = googleId,
        naverId = naverId,
    )

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(bytes)
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}
