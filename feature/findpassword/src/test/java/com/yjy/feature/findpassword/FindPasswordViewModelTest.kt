package com.yjy.feature.findpassword

import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.NetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.findpassword.model.FindPasswordUiAction
import com.yjy.feature.findpassword.model.FindPasswordUiEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FindPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: FindPasswordViewModel
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk(relaxed = true)

        viewModel = FindPasswordViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateEmail should validate email format`() = runTest {
        // Given
        val invalidEmail = "invalid-email"
        val validEmail = "valid@example.com"

        // When
        viewModel.processAction(FindPasswordUiAction.OnEmailUpdated(invalidEmail))
        val stateAfterInvalidEmail = viewModel.uiState.first()

        viewModel.processAction(FindPasswordUiAction.OnEmailUpdated(validEmail))
        val stateAfterValidEmail = viewModel.uiState.first()

        // Then
        assertEquals(expected = false, actual = stateAfterInvalidEmail.isValidEmailFormat)
        assertEquals(expected = true, actual = stateAfterValidEmail.isValidEmailFormat)
    }

    @Test
    fun `sendVerifyCode should send verification code`() = runTest {
        // Given
        val email = "valid@example.com"
        coEvery { authRepository.requestVerifyCode(email) } returns NetworkResult.Success(Unit)

        // When
        viewModel.processAction(FindPasswordUiAction.OnSendVerifyCodeClick(email))
        advanceUntilIdle()

        // Then
        coVerify { authRepository.requestVerifyCode(email) }
    }

    @Test
    fun `sendVerifyCode should handle unauthorized email`() = runTest {
        // Given
        val email = "unregistered@example.com"
        coEvery { authRepository.requestVerifyCode(email) } returns NetworkResult.Failure.HttpError(
            HttpStatusCodes.UNAUTHORIZED,
            message = null,
            body = "",
        )

        // When
        viewModel.processAction(FindPasswordUiAction.OnSendVerifyCodeClick(email))
        advanceUntilIdle()

        // Then
        val event = viewModel.uiEvent.first()
        assertEquals(expected = FindPasswordUiEvent.SendVerifyCodeFailure.UnregisteredEmail, actual = event)
    }

    @Test
    fun `sendVerifyCode should handle network error`() = runTest {
        // Given
        val email = "valid@example.com"
        val throwable = Throwable("Network error")
        coEvery { authRepository.requestVerifyCode(email) } returns NetworkResult.Failure.NetworkError(throwable)

        // When
        viewModel.processAction(FindPasswordUiAction.OnSendVerifyCodeClick(email))
        advanceUntilIdle()

        // Then
        val event = viewModel.uiEvent.first()
        assertEquals(expected = FindPasswordUiEvent.Failure.NetworkError, actual = event)
    }

    @Test
    fun `updateVerifyCode should validate code format and length`() = runTest {
        // Given
        val validCode = "123456"
        val invalidCode = "12abc"

        // When
        viewModel.processAction(FindPasswordUiAction.OnVerifyCodeUpdated(invalidCode))
        val stateAfterInvalidCode = viewModel.uiState.first()

        viewModel.processAction(FindPasswordUiAction.OnVerifyCodeUpdated(validCode))
        val stateAfterValidCode = viewModel.uiState.first()

        // Then
        assertEquals(expected = "", actual = stateAfterInvalidCode.verifyCode)
        assertEquals(expected = validCode, actual = stateAfterValidCode.verifyCode)
    }

    @Test
    fun `verifyCode should verify and handle success`() = runTest {
        // Given
        val email = "valid@example.com"
        val code = "123456"
        coEvery { authRepository.verifyCode(email, code) } returns NetworkResult.Success(Unit)

        // When
        viewModel.processAction(FindPasswordUiAction.OnEmailUpdated(email))
        viewModel.processAction(FindPasswordUiAction.OnVerifyCodeUpdated(code))
        advanceUntilIdle()

        // Then
        coVerify { authRepository.verifyCode(email, code) }
        val event = viewModel.uiEvent.first()
        assertEquals(expected = FindPasswordUiEvent.VerifySuccess, actual = event)
    }
}
