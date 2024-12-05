package com.yjy.common.designsystem.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

private const val SELECT_DISPOSE_DELAY = 50L

@Composable
fun SelectableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CustomColorProvider.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.labelSmall,
    textAlign: TextAlign = TextAlign.Start,
) {
    var isSelectionEnabled by remember { mutableStateOf(true) }
    var hasSelection by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = hasSelection) {
        isSelectionEnabled = false
        scope.launch {
            delay(SELECT_DISPOSE_DELAY)
            isSelectionEnabled = true
        }
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = CustomColorProvider.colorScheme.brandBright,
            backgroundColor = CustomColorProvider.colorScheme.brandBright.copy(alpha = 0.5f),
        ),
    ) {
        Box(
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { hasSelection = true },
                )
            },
        ) {
            if (isSelectionEnabled) {
                SelectionContainer {
                    Text(
                        text = text,
                        color = color,
                        style = style,
                        textAlign = textAlign,
                    )
                }
            } else {
                DisableSelection {
                    Text(
                        text = text,
                        color = color,
                        style = style,
                        textAlign = textAlign,
                    )
                }
                hasSelection = false
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
