package com.yjy.feature.changepassword

import com.yjy.common.network.NetworkResult
import com.yjy.data.auth.api.AuthRepository
import com.yjy.feature.changepassword.model.ChangePasswordUiAction
import com.yjy.feature.changepassword.model.ChangePasswordUiEvent
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
class ChangePasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: ChangePasswordViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk(relaxed = true)

        viewModel = ChangePasswordViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `showExitConfirmDialog should update state to show exit confirm dialog`() = runTest {
        // When
        viewModel.processAction(ChangePasswordUiAction.OnBackClick)

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = true, actual = state.shouldShowExitConfirmDialog)
    }

    @Test
    fun `dismissExitConfirmDialog should update state to hide exit confirm dialog`() = runTest {
        // Given
        viewModel.processAction(ChangePasswordUiAction.OnBackClick)

        // When
        viewModel.processAction(ChangePasswordUiAction.OnCancelExit)

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = false, actual = state.shouldShowExitConfirmDialog)
    }

    @Test
    fun `confirmExit should send CancelChangePassword event and dismiss exit confirm dialog`() = runTest {
        // Given
        viewModel.processAction(ChangePasswordUiAction.OnBackClick)

        // When
        viewModel.processAction(ChangePasswordUiAction.OnConfirmExit)

        // Then
        val event = viewModel.uiEvent.first()
        assertEquals(expected = ChangePasswordUiEvent.CancelChangePassword, actual = event)
        val state = viewModel.uiState.first()
        assertEquals(expected = false, actual = state.shouldShowExitConfirmDialog)
    }

    @Test
    fun `showChangeConfirmDialog should update state to show change confirm dialog`() = runTest {
        // When
        viewModel.processAction(ChangePasswordUiAction.OnChangeClick)

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = true, actual = state.shouldShowChangeConfirmDialog)
    }

    @Test
    fun `dismissChangeConfirmDialog should update state to hide change confirm dialog`() = runTest {
        // Given
        viewModel.processAction(ChangePasswordUiAction.OnChangeClick)

        // When
        viewModel.processAction(ChangePasswordUiAction.OnCancelChange)

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = false, actual = state.shouldShowChangeConfirmDialog)
    }

    @Test
    fun `changePassword should send ChangeSuccess event on success`() = runTest {
        // Given
        val password = "Password123"
        coEvery { authRepository.changePassword(password) } returns NetworkResult.Success(Unit)

        // When
        viewModel.processAction(ChangePasswordUiAction.OnConfirmChange(password))

        // Then
        val event = viewModel.uiEvent.first()
        assertEquals(expected = ChangePasswordUiEvent.ChangeSuccess, actual = event)
    }

    @Test
    fun `changePassword should handle network error`() = runTest {
        // Given
        val password = "Password123"
        val throwable = Throwable("Network error")
        coEvery { authRepository.changePassword(password) } returns NetworkResult.Failure.NetworkError(throwable)

        // When
        viewModel.processAction(ChangePasswordUiAction.OnConfirmChange(password))

        // Then
        val event = viewModel.uiEvent.first()
        assertEquals(expected = ChangePasswordUiEvent.Failure.NetworkError, actual = event)
    }

    @Test
    fun `updatePassword should not allow short password`() = runTest {
        // Given
        val shortPassword = "Pass1"

        // When
        viewModel.processAction(ChangePasswordUiAction.OnPasswordUpdated(shortPassword))

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = shortPassword, actual = state.password)
        assertEquals(expected = false, actual = state.isPasswordLongEnough)
    }

    @Test
    fun `updatePassword should not allow password without numbers`() = runTest {
        // Given
        val noNumberPassword = "Password"

        // When
        viewModel.processAction(ChangePasswordUiAction.OnPasswordUpdated(noNumberPassword))

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = noNumberPassword, actual = state.password)
        assertEquals(expected = false, actual = state.isPasswordContainNumber)
    }

    @Test
    fun `updatePassword should update uiState with valid password`() = runTest {
        // Given
        val validPassword = "Password123"

        // When
        viewModel.processAction(ChangePasswordUiAction.OnPasswordUpdated(validPassword))

        // Then
        val state = viewModel.uiState.first()
        assertEquals(expected = validPassword, actual = state.password)
        assertEquals(expected = true, actual = state.isPasswordLongEnough)
        assertEquals(expected = true, actual = state.isPasswordContainNumber)
    }
}