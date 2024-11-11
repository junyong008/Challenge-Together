package com.yjy.common.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int? = null,
    rightContent: @Composable (() -> Unit)? = null,
    onNavigationClick: () -> Unit = {},
    backgroundColor: Color = CustomColorProvider.colorScheme.background,
    contentColor: Color = CustomColorProvider.colorScheme.onBackground,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(top = 32.dp, bottom = 32.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
        ) {
            DebouncedIconButton(
                onClick = { onNavigationClick() },
            ) {
                Icon(
                    ImageVector.vectorResource(id = ChallengeTogetherIcons.Back),
                    contentDescription = stringResource(
                        id = R.string.common_designsystem_app_bar_back,
                    ),
                    tint = contentColor,
                )
            }
        }

        titleRes?.let {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = it),
                textAlign = TextAlign.Center,
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        rightContent?.let {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                it()
            }
        }
    }
}

@ComponentPreviews
@Composable
fun ChallengeTogetherTopAppBarPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherTopAppBar(
            titleRes = android.R.string.untitled,
            rightContent = {
                Text(text = "Info", color = CustomColorProvider.colorScheme.onBackground)
            },
            onNavigationClick = {},
        )
    }
}
