package com.yjy.data.auth.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.onSuccess
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.network.datasource.AuthDataSource
import com.yjy.data.network.request.auth.ChangePasswordRequest
import com.yjy.data.network.request.auth.EmailLoginRequest
import com.yjy.data.network.request.auth.EmailRequest
import com.yjy.data.network.request.auth.GoogleLoginRequest
import com.yjy.data.network.request.auth.GuestLoginRequest
import com.yjy.data.network.request.auth.KakaoLoginRequest
import com.yjy.data.network.request.auth.NaverLoginRequest
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
        guestId: String,
    ): NetworkResult<Unit> = authDataSource.signUp(
        signUpRequest = SignUpRequest(
            nickname = nickname,
            email = email,
            password = password.hash(),
            kakaoId = kakaoId,
            googleId = googleId,
            naverId = naverId,
            guestId = guestId.hash(),
        ),
    ).onSuccess {
        sessionDataSource.setLoggedIn(true)
    }

    override suspend fun emailLogin(email: String, password: String): NetworkResult<Unit> =
        authDataSource.emailLogin(
            emailLoginRequest = EmailLoginRequest(
                email = email,
                password = password.hash(),
            ),
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }

    override suspend fun kakaoLogin(kakaoId: String): NetworkResult<Unit> =
        authDataSource.kakaoLogin(
            kakaoLoginRequest = KakaoLoginRequest(kakaoId),
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }

    override suspend fun googleLogin(googleId: String): NetworkResult<Unit> =
        authDataSource.googleLogin(
            googleLoginRequest = GoogleLoginRequest(googleId),
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }

    override suspend fun naverLogin(naverId: String): NetworkResult<Unit> =
        authDataSource.naverLogin(
            naverLoginRequest = NaverLoginRequest(naverId),
        ).onSuccess {
            sessionDataSource.setLoggedIn(true)
        }

    override suspend fun guestLogin(guestId: String): NetworkResult<Unit> =
        authDataSource.guestLogin(
            guestLoginRequest = GuestLoginRequest(guestId.hash()),
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
            changePasswordRequest = ChangePasswordRequest(password = password.hash()),
        ).onSuccess {
            sessionDataSource.setLoggedIn(false)
            sessionDataSource.setToken(null)
        }

    override suspend fun deleteAccount(): NetworkResult<Unit> = authDataSource.deleteAccount()

    private fun String.hash(algorithm: String = "SHA-256"): String {
        if (this.isBlank()) return ""
        val bytes = this.toByteArray()
        val md = MessageDigest.getInstance(algorithm)
        val hashedBytes = md.digest(bytes)
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}
