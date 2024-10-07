package com.yjy.feature.signup

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.data.repository.AuthRepository
import com.yjy.feature.signup.model.SignUpUiAction
import com.yjy.feature.signup.model.SignUpUiEvent
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
class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        authRepository = mockk()

        viewModel = SignUpViewModel(
            authRepository = authRepository,
            saveStateHandle = mockk(relaxed = true)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signUp should trigger SignUpSuccess event on success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123"
        val nickname = "testUser"
        coEvery { authRepository.signUp(any(), any(), any(), any(), any(), any()) } returns NetworkResult.Success(Unit)

        // When
        viewModel.processAction(
            SignUpUiAction.OnStartClick(
                nickname = nickname,
                email = email,
                password = password,
            )
        )

        // Then
        assertEquals(
            expected = SignUpUiEvent.SignUpSuccess,
            actual = viewModel.uiEvent.first(),
        )
    }

    @Test
    fun `checkEmailDuplicate should trigger EmailPasswordVerified event on success`() = runTest {
        // Given
        val email = "test@example.com"
        coEvery { authRepository.checkEmailDuplicate(email) } returns NetworkResult.Success(Unit)

        // When
        viewModel.processAction(SignUpUiAction.OnEmailPasswordContinueClick(email))

        // Then
        assertEquals(
            expected = SignUpUiEvent.EmailPasswordVerified,
            actual = viewModel.uiEvent.first(),
        )
    }

    @Test
    fun `updateEmail should update uiState with valid email`() = runTest {
        // Given
        val email = "test@example.com"

        // When
        viewModel.processAction(SignUpUiAction.OnEmailUpdated(email))

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
    fun `updateEmail should not allow invalid email format`() = runTest {
        // Given
        val invalidEmail = "invalid-email"

        // When
        viewModel.processAction(SignUpUiAction.OnEmailUpdated(invalidEmail))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = invalidEmail,
            actual = updatedState.email,
        )
        assertEquals(
            expected = false,
            actual = updatedState.isValidEmailFormat,
        )
    }

    @Test
    fun `updatePassword should update uiState with valid password`() = runTest {
        // Given
        val password = "Password123"

        // When
        viewModel.processAction(SignUpUiAction.OnPasswordUpdated(password))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = password,
            actual = updatedState.password,
        )
        assertEquals(
            expected = true,
            actual = updatedState.isPasswordLongEnough,
        )
        assertEquals(
            expected = true,
            actual = updatedState.isPasswordContainNumber,
        )
    }

    @Test
    fun `updatePassword should not allow short password`() = runTest {
        // Given
        val shortPassword = "Pass1"

        // When
        viewModel.processAction(SignUpUiAction.OnPasswordUpdated(shortPassword))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = shortPassword,
            actual = updatedState.password,
        )
        assertEquals(
            expected = false,
            actual = updatedState.isPasswordLongEnough,
        )
    }

    @Test
    fun `updatePassword should not allow password without numbers`() = runTest {
        // Given
        val passwordWithoutNumber = "Password"

        // When
        viewModel.processAction(SignUpUiAction.OnPasswordUpdated(passwordWithoutNumber))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = passwordWithoutNumber,
            actual = updatedState.password,
        )
        assertEquals(
            expected = false,
            actual = updatedState.isPasswordContainNumber,
        )
    }

    @Test
    fun `canTryContinueToNickname should be true when all conditions are met`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "Password123"

        // When
        viewModel.processAction(SignUpUiAction.OnEmailUpdated(email))
        viewModel.processAction(SignUpUiAction.OnPasswordUpdated(password))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = true,
            actual = updatedState.canTryContinueToNickname,
        )
    }

    @Test
    fun `canTryStart should be true when valid nickname is provided`() = runTest {
        // Given
        val nickname = "testUser"

        // When
        viewModel.processAction(SignUpUiAction.OnNicknameUpdated(nickname))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = true,
            actual = updatedState.canTryStart,
        )
    }

    @Test
    fun `updateNickname should not allow nickname with invalid characters`() = runTest {
        // Given
        val invalidNickname = "nickname!"

        // When
        viewModel.processAction(SignUpUiAction.OnNicknameUpdated(invalidNickname))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = "",
            actual = updatedState.nickname,
        )
        assertEquals(
            expected = false,
            actual = updatedState.canTryStart,
        )
    }

    @Test
    fun `updateNickname should not allow nickname with only consonant or vowel`() = runTest {
        // Given
        val invalidNickname = "ㄱㅏㅏ"

        // When
        viewModel.processAction(SignUpUiAction.OnNicknameUpdated(invalidNickname))

        // Then
        val updatedState = viewModel.uiState.first()
        assertEquals(
            expected = invalidNickname,
            actual = updatedState.nickname,
        )
        assertEquals(
            expected = true,
            actual = updatedState.isNicknameHasOnlyConsonantOrVowel,
        )
        assertEquals(
            expected = false,
            actual = updatedState.canTryStart,
        )
    }
}