package com.yjy.common.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun TitleWithDescription(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    descriptionStyle: TextStyle = MaterialTheme.typography.labelSmall,
    titleColor: Color = CustomColorProvider.colorScheme.onBackground,
    descriptionColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
    spacing: Dp = 8.dp,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = titleRes),
            style = titleStyle,
            color = titleColor,
        )
        Spacer(modifier = Modifier.height(spacing))
        Text(
            text = stringResource(id = descriptionRes),
            style = descriptionStyle,
            color = descriptionColor,
        )
    }
}

@ThemePreviews
@Composable
fun TitleWithDescriptionPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.height(100.dp)) {
            TitleWithDescription(
                titleRes = android.R.string.untitled,
                descriptionRes = android.R.string.untitled,
            )
        }
    }
}
