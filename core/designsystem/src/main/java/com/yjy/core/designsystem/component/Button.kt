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
import com.yjy.core.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = CustomColorProvider.colorScheme.brand,
    borderColor: Color = CustomColorProvider.colorScheme.brand,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor,
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
                contentPadding = ButtonDefaults.ContentPadding,
            ) {
                Text(
                    text = "Button",
                )
            }
        }
    }
}