package com.yjy.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.ComponentPreviews
import com.yjy.core.designsystem.R
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int? = null,
    rightContent: @Composable (() -> Unit)? = null,
    onNavigationClick: () -> Unit = {},
    backgroundColor: Color = CustomColorProvider.colorScheme.background,
    contentColor: Color = CustomColorProvider.colorScheme.onBackground,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(top = 32.dp, bottom = 32.dp, start = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = { onNavigationClick() },
        ) {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Back),
                contentDescription = stringResource(
                    id = R.string.core_designsystem_app_bar_back,
                ),
                tint = contentColor,
            )
        }
        titleRes?.let {
            Text(
                text = stringResource(id = it),
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        rightContent?.let { it() }
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
