package com.yjy.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ManualTimeWarning(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(CustomColorProvider.colorScheme.red.copy(alpha = 0.8f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.common_ui_manual_time_warning),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Black,
        )
    }
}

@ComponentPreviews
@Composable
fun ManualTimeWarningPreview() {
    ChallengeTogetherTheme {
        ManualTimeWarning()
    }
}
