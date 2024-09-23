package com.yjy.feature.login

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk()

        viewModel = LoginViewModel(
            authRepository = authRepository,
        )
    }

    @Test
    fun `login with invalid email format should trigger InvalidEmailFormat event`() = runTest {
        // Given
        val invalidEmail = "invalidEmail"
        val password = "ValidPassword123"

        // When
        viewModel.login(invalidEmail, password)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.InvalidEmailFormat,
            actual = viewModel.uiEvent.first()
        )
    }

    @Test
    fun `login with empty password should trigger EmptyPassword event`() = runTest {
        // Given
        val email = "user@example.com"
        val emptyPassword = ""

        // When
        viewModel.login(email, emptyPassword)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.EmptyPassword,
            actual = viewModel.uiEvent.first()
        )
    }

    @Test
    fun `login with valid credentials should trigger Success event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        coEvery { authRepository.login(email, password) } returns NetworkResult.Success(Unit)

        // When
        viewModel.login(email, password)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginSuccess,
            actual = viewModel.uiEvent.first()
        )
    }

    @Test
    fun `login with non-existing user should trigger UserNotFound event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "WrongPassword"
        coEvery {
            authRepository.login(email, password)
        } returns NetworkResult.Failure.HttpError(code = 404, message = null, body = "")

        // When
        viewModel.login(email, password)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.UserNotFound,
            actual = viewModel.uiEvent.first()
        )
    }

    @Test
    fun `login with server error should trigger ServerError event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        coEvery {
            authRepository.login(email, password)
        } returns NetworkResult.Failure.HttpError(code = 500, message = null, body = "")

        // When
        viewModel.login(email, password)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.ServerError,
            actual = viewModel.uiEvent.first()
        )
    }

    @Test
    fun `login with unknown error should trigger Unknown event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        val unknownException = Exception("Unexpected error")
        coEvery {
            authRepository.login(email, password)
        } returns NetworkResult.Failure.UnknownApiError(unknownException)

        // When
        viewModel.login(email, password)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.Unknown,
            actual = viewModel.uiEvent.first()
        )
    }
}