package com.yjy.data.auth.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.network.datasource.AuthDataSource
import com.yjy.data.network.request.ChangePasswordRequest
import com.yjy.data.network.request.EmailLoginRequest
import com.yjy.data.network.request.SignUpRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRepositoryImplTest {

    private lateinit var authDataSource: AuthDataSource
    private lateinit var sessionDataSource: SessionDataSource
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setup() {
        authDataSource = mockk()
        sessionDataSource = mockk(relaxed = true)

        authRepository = AuthRepositoryImpl(
            authDataSource = authDataSource,
            sessionDataSource = sessionDataSource,
        )
    }

    @Test
    fun `email login should hash password and call authDataSource with SHA-256 hashed password`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val hashedPassword = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
        coEvery {
            authDataSource.emailLogin(EmailLoginRequest(email, hashedPassword))
        } returns NetworkResult.Success(Unit)
        coEvery { sessionDataSource.setLoggedIn(any()) } returns Unit

        // When
        val result = authRepository.emailLogin(email, password)

        // Then
        coVerify { authDataSource.emailLogin(EmailLoginRequest(email, hashedPassword)) }
        coVerify { sessionDataSource.setLoggedIn(true) }
        assertEquals(NetworkResult.Success(Unit), result)
    }

    @Test
    fun `checkEmailDuplicate should call authDataSource and return its result`() = runTest {
        // Given
        val email = "test@example.com"
        coEvery { authDataSource.checkEmailDuplicate(email) } returns NetworkResult.Success(Unit)

        // When
        val result = authRepository.checkEmailDuplicate(email)

        // Then
        coVerify { authDataSource.checkEmailDuplicate(email) }
        assertEquals(NetworkResult.Success(Unit), result)
    }

    @Test
    fun `signUp should hash password and call authDataSource with SHA-256 hashed password`() = runTest {
        // Given
        val nickname = "nickname"
        val email = "test@example.com"
        val password = "password123"
        val kakaoId = ""
        val googleId = ""
        val naverId = ""
        val hashedPassword = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
        coEvery {
            authDataSource.signUp(SignUpRequest(nickname, email, hashedPassword, kakaoId, googleId, naverId))
        } returns NetworkResult.Success(Unit)
        coEvery { sessionDataSource.setLoggedIn(any()) } returns Unit

        // When
        val result = authRepository.signUp(nickname, email, password, kakaoId, googleId, naverId)

        // Then
        coVerify { authDataSource.signUp(SignUpRequest(nickname, email, hashedPassword, kakaoId, googleId, naverId)) }
        coVerify { sessionDataSource.setLoggedIn(true) }
        assertEquals(NetworkResult.Success(Unit), result)
    }

    @Test
    fun `changePassword should update session and user preferences on success`() = runTest {
        // Given
        val password = "password123"
        val hashedPassword = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
        coEvery { authDataSource.changePassword(ChangePasswordRequest(hashedPassword)) } returns NetworkResult.Success(Unit)
        coEvery { sessionDataSource.setToken(any()) } returns Unit
        coEvery { sessionDataSource.setLoggedIn(any()) } returns Unit

        // When
        val result = authRepository.changePassword(password)

        // Then
        coVerify { authDataSource.changePassword(ChangePasswordRequest(hashedPassword)) }
        coVerify { sessionDataSource.setToken(null) }
        coVerify { sessionDataSource.setLoggedIn(false) }
        assertEquals(NetworkResult.Success(Unit), result)
    }
}
