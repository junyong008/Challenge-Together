package com.yjy.core.designsystem.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.R
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherBottomAppBar(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    showContinueButton: Boolean = true,
    enableContinueButton: Boolean = true,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    backgroundColor: Color = CustomColorProvider.colorScheme.background,
    contentColor: Color = CustomColorProvider.colorScheme.onBackground,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(top = 32.dp, bottom = 32.dp, start = 20.dp, end = 32.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        if (showBackButton) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(
                        onClick = onBackClick,
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = ChallengeTogetherIcons.Back),
                    contentDescription = stringResource(
                        id = R.string.core_designsystem_app_bar_back,
                    ),
                    tint = contentColor,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.core_designsystem_app_bar_back),
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        if (showContinueButton) {
            ChallengeTogetherButton(
                onClick = onContinueClick,
                enabled = enableContinueButton && isLoading.not(),
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .widthIn(min = 120.dp)
                    .height(50.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.core_designsystem_app_bar_continue),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun ChallengeTogetherBottomAppBarPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.height(130.dp)) {
            ChallengeTogetherBottomAppBar(
                onBackClick = {},
                onContinueClick = {},
                isLoading = false,
            )
        }
    }
}