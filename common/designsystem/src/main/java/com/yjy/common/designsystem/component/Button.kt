package com.yjy.common.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    containerColor: Color = CustomColorProvider.colorScheme.brandDim,
    contentColor: Color = CustomColorProvider.colorScheme.onBrandDim,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = CustomColorProvider.colorScheme.disable,
            disabledContentColor = CustomColorProvider.colorScheme.onDisable,
        ),
        shape = shape,
        content = content,
    )
}

@ThemePreviews
@Composable
fun ChallengeTogetherButtonPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            ChallengeTogetherButton(
                onClick = {},
                enabled = true,
            ) {
                Text(
                    text = "Button",
                )
            }
        }
    }
}
