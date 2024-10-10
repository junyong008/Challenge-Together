package com.yjy.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.ComponentPreviews
import com.yjy.core.designsystem.R
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    placeholderText: String = "",
    shape: Shape = MaterialTheme.shapes.medium,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(color = CustomColorProvider.colorScheme.onSurface),
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = CustomColorProvider.colorScheme.onSurface,
    placeholderColor: Color = CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.2f),
    borderColor: Color = Color.Transparent,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
    shouldHidePassword: Boolean = false,
    enabled: Boolean = true,
) {
    var leadingIconsWidth by remember { mutableStateOf(0.dp) }
    var trailingIconsWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .height(50.dp)
            .background(
                color = if (enabled) {
                    backgroundColor
                } else {
                    CustomColorProvider.colorScheme.disable
                },
                shape = shape,
            )
            .border(1.dp, borderColor, shape = shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .onGloballyPositioned { coordinates ->
                        leadingIconsWidth = with(density) { coordinates.size.width.toDp() }
                    },
            ) {
                leadingIcon()
            }
        }

        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = CustomColorProvider.colorScheme.brandBright,
                backgroundColor = CustomColorProvider.colorScheme.brandBright.copy(alpha = 0.5f),
            ),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                maxLines = maxLines,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                textStyle = textStyle.copy(color = textColor, textAlign = textAlign),
                cursorBrush = SolidColor(CustomColorProvider.colorScheme.brand),
                enabled = enabled,
                visualTransformation = if (shouldHidePassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (leadingIcon != null) leadingIconsWidth + 8.dp else 0.dp,
                        end = if (trailingIcon != null) trailingIconsWidth + 8.dp else 0.dp,
                    ),
            ) { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholderText,
                        style = textStyle.copy(color = placeholderColor, textAlign = textAlign),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                    )
                }
                innerTextField()
            }
        }

        if (trailingIcon != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .onGloballyPositioned { coordinates ->
                        trailingIconsWidth = with(density) { coordinates.size.width.toDp() }
                    },
            ) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun SingleLineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIconColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    shape: Shape = MaterialTheme.shapes.medium,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(color = CustomColorProvider.colorScheme.onSurface),
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = CustomColorProvider.colorScheme.onSurface,
    placeholderText: String = "",
    placeholderColor: Color = CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.2f),
    borderColor: Color = Color.Transparent,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
) {
    ChallengeTogetherTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (value.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = ChallengeTogetherIcons.Cancel),
                    contentDescription = stringResource(
                        id = R.string.core_designsystem_clear_content_description,
                    ),
                    tint = trailingIconColor,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { if (enabled) onValueChange("") },
                )
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        shape = shape,
        textStyle = textStyle,
        textAlign = textAlign,
        textColor = textColor,
        placeholderText = placeholderText,
        placeholderColor = placeholderColor,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIconColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    shape: Shape = MaterialTheme.shapes.medium,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(color = CustomColorProvider.colorScheme.onSurface),
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = CustomColorProvider.colorScheme.onSurface,
    placeholderText: String = "",
    placeholderColor: Color = CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.2f),
    borderColor: Color = Color.Transparent,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
) {
    var shouldHidePassword by rememberSaveable { mutableStateOf(true) }

    ChallengeTogetherTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (value.isNotEmpty()) {
                Row {
                    Icon(
                        painter = painterResource(
                            id = if (shouldHidePassword) {
                                ChallengeTogetherIcons.VisibilityOff
                            } else {
                                ChallengeTogetherIcons.Visibility
                            },
                        ),
                        contentDescription = if (shouldHidePassword) {
                            stringResource(id = R.string.core_designsystem_input_password_show_content_description)
                        } else {
                            stringResource(id = R.string.core_designsystem_input_password_hide_content_description)
                        },
                        tint = trailingIconColor,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                shouldHidePassword = !shouldHidePassword
                            },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.Cancel),
                        contentDescription = stringResource(
                            id = R.string.core_designsystem_clear_content_description,
                        ),
                        tint = trailingIconColor,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable { if (enabled) onValueChange("") },
                    )
                }
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        shape = shape,
        textStyle = textStyle,
        textAlign = textAlign,
        textColor = textColor,
        placeholderText = placeholderText,
        placeholderColor = placeholderColor,
        borderColor = borderColor,
        backgroundColor = backgroundColor,
        shouldHidePassword = shouldHidePassword,
    )
}

@ComponentPreviews
@Composable
fun ChallengeTogetherTextFieldPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherTextField(
            value = "",
            onValueChange = {},
            placeholderText = "Enter text...",
            leadingIcon = {
                Icon(
                    painter = painterResource(id = ChallengeTogetherIcons.Lock),
                    contentDescription = "Lock Icon",
                    tint = CustomColorProvider.colorScheme.onSurface
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = ChallengeTogetherIcons.Visibility),
                    contentDescription = "Visibility Icon",
                    tint = CustomColorProvider.colorScheme.onSurface
                )
            },
            singleLine = true,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
    }
}
