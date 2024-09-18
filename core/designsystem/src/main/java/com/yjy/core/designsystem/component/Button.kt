package com.yjy.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme

@Composable
fun ChallengeTogetherOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.outline,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = contentPadding,
        content = content,
    )
}

@ThemePreviews
@Composable
fun ChallengeTogetherOutlinedButtonPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            ChallengeTogetherOutlinedButton(
                onClick = {},
                modifier = Modifier,
                enabled = true,
                contentPadding = ButtonDefaults.ContentPadding,
            ) {
                Text(text = "Button")
            }
        }
    }
}