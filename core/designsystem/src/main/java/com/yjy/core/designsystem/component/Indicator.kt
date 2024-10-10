package com.yjy.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider

@Composable
fun ConditionIndicator(
    text: String,
    isMatched: Boolean,
    modifier: Modifier = Modifier,
    unMatchColor: Color = CustomColorProvider.colorScheme.disable,
    matchColor: Color = CustomColorProvider.colorScheme.brand,
) {
    val icon = if (isMatched) ChallengeTogetherIcons.Check else ChallengeTogetherIcons.UnCheck
    val color = if (isMatched) matchColor else unMatchColor

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(top = 8.dp),
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@ThemePreviews
@Composable
fun ConditionIndicatorPreviewMatched() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.height(70.dp)) {
            ConditionIndicator(
                text = "조건 충족",
                isMatched = true,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
fun ConditionIndicatorPreviewUnmatched() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.height(70.dp)) {
            ConditionIndicator(
                text = "조건 미충족",
                isMatched = false,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
