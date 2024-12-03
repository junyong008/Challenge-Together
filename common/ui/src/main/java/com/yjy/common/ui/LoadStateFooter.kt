package com.yjy.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme

@Composable
fun LoadStateFooter(
    state: FooterState,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is FooterState.Loading -> LoadingWheel()
            is FooterState.Error -> ErrorItem(onClickRetry = onClickRetry)
            FooterState.Idle -> Unit
        }
    }
}

sealed interface FooterState {
    data object Idle : FooterState
    data object Loading : FooterState
    data object Error : FooterState
}

@ThemePreviews
@Composable
fun LoadStateFooterPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.height(150.dp)) {
            LoadStateFooter(
                state = FooterState.Error,
                onClickRetry = {},
            )
        }
    }
}
