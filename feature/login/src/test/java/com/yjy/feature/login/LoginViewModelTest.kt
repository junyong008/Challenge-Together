package com.yjy.feature.login

import com.yjy.core.common.constants.AuthConst.MAX_EMAIL_LENGTH
import com.yjy.core.common.constants.AuthConst.MAX_PASSWORD_LENGTH
import com.yjy.core.common.network.HttpStatusCodes.UNAUTHORIZED
import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
import com.yjy.feature.login.model.LoginUiAction
import com.yjy.feature.login.model.LoginUiEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials should trigger Success event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        coEvery { authRepository.emailLogin(email, password) } returns NetworkResult.Success(Unit)

        // When
        val action = LoginUiAction.OnEmailLoginClick(email, password)
        viewModel.processAction(action)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginSuccess,
            actual = viewModel.uiEvent.first(),
        )
    }

    @Test
    fun `login with non-existing user should trigger UserNotFound event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "WrongPassword"
        coEvery {
            authRepository.emailLogin(email, password)
        } returns NetworkResult.Failure.HttpError(code = UNAUTHORIZED, message = null, body = "")

        // When
        val action = LoginUiAction.OnEmailLoginClick(email, password)
        viewModel.processAction(action)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.UserNotFound,
            actual = viewModel.uiEvent.first(),
        )
    }

    @Test
    fun `login with error should trigger Error event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        coEvery {
            authRepository.emailLogin(email, password)
        } returns NetworkResult.Failure.HttpError(code = 500, message = null, body = "")

        // When
        val action = LoginUiAction.OnEmailLoginClick(email, password)
        viewModel.processAction(action)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.UnknownError,
            actual = viewModel.uiEvent.first(),
        )
    }

    @Test
    fun `email update should update uiState with valid email format`() = runTest {
        // Given
        val email = "user@example.com"

        // When
        viewModel.processAction(LoginUiAction.OnEmailUpdated(email))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = email,
            actual = updatedState.email,
        )
        assertEquals(
            expected = true,
            actual = updatedState.isValidEmailFormat,
        )
    }

    @Test
    fun `password update should update uiState`() = runTest {
        // Given
        val password = "ValidPassword123"

        // When
        viewModel.processAction(LoginUiAction.OnPasswordUpdated(password))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = password,
            actual = updatedState.password,
        )
    }

    @Test
    fun `canTryLogin should be true when email and password are valid`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"

        // When
        viewModel.processAction(LoginUiAction.OnEmailUpdated(email))
        viewModel.processAction(LoginUiAction.OnPasswordUpdated(password))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = true,
            actual = updatedState.canTryLogin,
        )
    }

    @Test
    fun `canTryLogin should be false when email is invalid`() = runTest {
        // Given
        val email = "invalid-email"
        val password = "ValidPassword123"

        // When
        viewModel.processAction(LoginUiAction.OnEmailUpdated(email))
        viewModel.processAction(LoginUiAction.OnPasswordUpdated(password))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = false,
            actual = updatedState.canTryLogin,
        )
    }

    @Test
    fun `canTryLogin should be false when password is empty`() = runTest {
        // Given
        val email = "user@example.com"
        val password = ""

        // When
        viewModel.processAction(LoginUiAction.OnEmailUpdated(email))
        viewModel.processAction(LoginUiAction.OnPasswordUpdated(password))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = false,
            actual = updatedState.canTryLogin,
        )
    }

    @Test
    fun `login with network error should trigger NetworkError event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        val throwable = Throwable("Network error")
        coEvery { authRepository.emailLogin(email, password) } returns NetworkResult.Failure.NetworkError(throwable)

        // When
        val action = LoginUiAction.OnEmailLoginClick(email, password)
        viewModel.processAction(action)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.NetworkError,
            actual = viewModel.uiEvent.first(),
        )
    }

    @Test
    fun `password update should not update when password exceeds max length`() = runTest {
        // Given
        val longPassword = "a".repeat(MAX_PASSWORD_LENGTH + 1)

        // When
        viewModel.processAction(LoginUiAction.OnPasswordUpdated(longPassword))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = "",
            actual = updatedState.password,
        )
    }

    @Test
    fun `email update should not update when email exceeds max length`() = runTest {
        // Given
        val longEmail = "a".repeat(MAX_EMAIL_LENGTH + 1) + "@example.com"

        // When
        viewModel.processAction(LoginUiAction.OnEmailUpdated(longEmail))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = "",
            actual = updatedState.email,
        )
    }

    @Test
    fun `login with unknown error should trigger UnknownError event`() = runTest {
        // Given
        val email = "user@example.com"
        val password = "ValidPassword123"
        val throwable = Throwable("Unknown error")
        coEvery { authRepository.emailLogin(email, password) } returns NetworkResult.Failure.UnknownApiError(throwable)

        // When
        val action = LoginUiAction.OnEmailLoginClick(email, password)
        viewModel.processAction(action)

        // Then
        assertEquals(
            expected = LoginUiEvent.LoginFailure.UnknownError,
            actual = viewModel.uiEvent.first(),
        )
    }
}
