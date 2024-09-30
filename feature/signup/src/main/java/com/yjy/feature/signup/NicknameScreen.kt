package com.yjy.feature.signup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.ui.DevicePreviews

@Composable
internal fun NicknameRoute(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    NicknameScreen(
        modifier = modifier,
    )
}

@Composable
internal fun NicknameScreen(
    modifier: Modifier = Modifier,
) {
}

@DevicePreviews
@Composable
fun NicknameScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            NicknameScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
