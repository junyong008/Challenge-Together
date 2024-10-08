package com.yjy.feature.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.feature.login.model.LoginUiEvent
import com.yjy.feature.login.model.LoginUiState
import com.yjy.feature.login.navigation.LoginStrings
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun invalid_email_input_should_display_error_message() {
        // Given
        val uiState = LoginUiState(isValidEmailFormat = false)

        // When
        composeTestRule.setContent {
            LoginScreen(
                uiState = uiState,
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(LoginStrings.feature_login_invalid_email_format))
            .assertIsDisplayed()
    }

    @Test
    fun user_not_found_event_should_trigger_user_not_found_snackbar() {
        // Given
        val uiEvent = LoginUiEvent.LoginFailure.UserNotFound

        // When
        var isSnackbarTriggered = false
        var snackbarType: SnackbarType? = null
        var snackbarMessage = ""
        composeTestRule.setContent {
            LoginScreen(
                uiEvent = flowOf(uiEvent),
                onShowSnackbar = { type, message ->
                    isSnackbarTriggered = true
                    snackbarType = type
                    snackbarMessage = message
                },
            )
        }

        // Then
        assert(isSnackbarTriggered)
        assertEquals(SnackbarType.ERROR, snackbarType)
        assert(snackbarMessage.contains(composeTestRule.activity.getString(LoginStrings.feature_login_user_not_found)))
    }

    @Test
    fun error_event_should_trigger_error_snackbar() {
        // Given
        val uiEvent = LoginUiEvent.LoginFailure.Error

        // Given & When
        var isSnackbarTriggered = false
        var snackbarType: SnackbarType? = null
        var snackbarMessage = ""
        composeTestRule.setContent {
            LoginScreen(
                uiEvent = flowOf(uiEvent),
                onShowSnackbar = { type, message ->
                    isSnackbarTriggered = true
                    snackbarType = type
                    snackbarMessage = message
                },
            )
        }

        // Then
        assert(isSnackbarTriggered)
        assertEquals(SnackbarType.ERROR, snackbarType)
        assert(snackbarMessage.contains(composeTestRule.activity.getString(LoginStrings.feature_login_error)))
    }

    @Test
    fun login_button_should_be_enabled_when_canTryLogin_is_true_and_loading_is_false() {
        // Given
        val uiState = LoginUiState(
            canTryLogin = true,
            isLoading = false,
        )

        // When
        composeTestRule.setContent {
            LoginScreen(uiState = uiState)
        }

        // Then
        composeTestRule
            .onNodeWithTag("loginButton")
            .assertIsEnabled()
    }

    @Test
    fun login_button_should_be_disabled_when_canTryLogin_is_false_or_loading_is_true() {
        // Given
        val uiState = LoginUiState(
            canTryLogin = false,
            isLoading = true,
        )

        // When
        composeTestRule.setContent {
            LoginScreen(uiState = uiState)
        }

        // Then
        composeTestRule
            .onNodeWithTag("loginButton")
            .assertIsNotEnabled()
    }
}
