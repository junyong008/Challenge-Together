package com.yjy.common.designsystem.component

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    useSingleClick: Boolean = true,
) {
    val annotatedString = when (text) {
        is String -> AnnotatedString(text)
        is AnnotatedString -> text
        else -> throw IllegalArgumentException("Unsupported text type")
    }

    val clickableModifier = if (useSingleClick) {
        Modifier.clickableSingle(enabled = enabled, onClick = onClick)
    } else {
        Modifier.clickable(enabled = enabled, onClick = onClick)
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
            .then(clickableModifier)
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
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val androidClipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    fun shouldIgnore(keyEvent: KeyEvent): Boolean {
        return isFocused && keyEvent.key == Key.Back && keyEvent.type == KeyEventType.KeyUp
    }

    DisposableEffect(androidClipboardManager) {
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            if (isFocused) focusManager.clearFocus()
        }

        androidClipboardManager.addPrimaryClipChangedListener(listener)

        onDispose {
            androidClipboardManager.removePrimaryClipChangedListener(listener)
        }
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
                    .onFocusChanged { isFocused = it.isFocused }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (isFocused) focusManager.clearFocus()
                            },
                        )
                    }
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

@Composable
fun BulletText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
    bulletColor: Color = color,
    style: TextStyle = MaterialTheme.typography.labelSmall,
) {
    val bullet = "•  "
    val bulletWidth = with(LocalDensity.current) {
        style.fontSize.toPx().toSp()
    }

    Text(
        text = buildAnnotatedString {
            withStyle(
                ParagraphStyle(
                    textIndent = TextIndent(
                        firstLine = 0.sp,
                        restLine = bulletWidth,
                    ),
                ),
            ) {
                withStyle(style = SpanStyle(color = bulletColor)) {
                    append(bullet)
                }
                append(text)
            }
        },
        modifier = modifier,
        color = color,
        style = style,
    )
}

@ComponentPreviews
@Composable
fun ClickableTextPreview() {
    ChallengeTogetherTheme {
        ClickableText(text = "Clickable Text", onClick = {})
    }
}
