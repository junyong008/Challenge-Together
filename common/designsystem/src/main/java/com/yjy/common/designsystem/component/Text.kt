package com.yjy.common.designsystem.component

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun SelectableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CustomColorProvider.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.labelSmall,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    fun shouldIgnore(keyEvent: KeyEvent): Boolean {
        return isFocused && keyEvent.key == Key.Back && keyEvent.type == KeyEventType.KeyUp
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = CustomColorProvider.colorScheme.brandBright,
            backgroundColor = CustomColorProvider.colorScheme.brandBright.copy(alpha = 0.5f),
        ),
    ) {
        Box(modifier = modifier) {
            SelectionContainer(
                modifier = Modifier
                    .focusable()
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused }
                    .onPreviewKeyEvent { shouldIgnore(it) },
            ) {
                Text(
                    text = text,
                    color = color,
                    style = style,
                    overflow = overflow,
                    maxLines = maxLines,
                    textAlign = textAlign,
                    onTextLayout = onTextLayout,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
fun ClickableTextPreview() {
    ChallengeTogetherTheme {
        ClickableText(text = "Clickable Text", onClick = {})
    }
}
