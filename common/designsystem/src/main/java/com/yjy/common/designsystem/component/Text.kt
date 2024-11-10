package com.yjy.common.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ClickableText(
    text: Any,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = CustomColorProvider.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.labelSmall,
    textAlign: TextAlign = TextAlign.Start,
    textDecoration: TextDecoration? = TextDecoration.Underline,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    val annotatedString = when (text) {
        is String -> AnnotatedString(text)
        is AnnotatedString -> text
        else -> throw IllegalArgumentException("Unsupported text type")
    }

    Text(
        text = annotatedString,
        color = if (enabled) color else CustomColorProvider.colorScheme.disable,
        style = style.copy(
            textDecoration = textDecoration,
        ),
        textAlign = textAlign,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickableSingle(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(contentPadding),
    )
}

@ComponentPreviews
@Composable
fun ClickableTextPreview() {
    ChallengeTogetherTheme {
        ClickableText(text = "Clickable Text", onClick = {})
    }
}
