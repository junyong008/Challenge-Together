package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.datasource.AuthDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRepositoryImplTest {

    private lateinit var authDataSource: AuthDataSource
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setup() {
        authDataSource = mockk()
        authRepository = AuthRepositoryImpl(authDataSource)
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
            authDataSource.signUp(nickname, email, hashedPassword, kakaoId, googleId, naverId)
        } returns NetworkResult.Success(Unit)

        // When
        val result = authRepository.signUp(nickname, email, password, kakaoId, googleId, naverId)

        // Then
        coVerify { authDataSource.signUp(nickname, email, hashedPassword, kakaoId, googleId, naverId) }
        assertEquals(NetworkResult.Success(Unit), result)
    }
}