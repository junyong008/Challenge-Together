package com.yjy.common.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

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
    borderColor: Color? = null,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
    enabled: Boolean = true,
    contentAlignment: Alignment = Alignment.CenterStart,
    isPassword: Boolean = false,
    passwordIconColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Row(
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
            .then(
                if (borderColor != null) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = shape,
                    )
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
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
                visualTransformation = if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                modifier = Modifier.weight(1f),
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 14.dp),
                    contentAlignment = contentAlignment,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = textStyle.copy(color = placeholderColor, textAlign = textAlign),
                        )
                    }
                    innerTextField()
                }
            }
        }

        if (isPassword && value.isNotEmpty()) {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible) {
                            ChallengeTogetherIcons.Visibility
                        } else {
                            ChallengeTogetherIcons.VisibilityOff
                        }
                    ),
                    contentDescription = if (passwordVisible) {
                        stringResource(id = R.string.common_designsystem_input_password_hide_content_description)
                    } else {
                        stringResource(id = R.string.common_designsystem_input_password_show_content_description)
                    },
                    tint = passwordIconColor
                )
            }
        }

        if (trailingIcon != null) {
            trailingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(16.dp))
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
    contentAlignment: Alignment = Alignment.CenterStart,
    isPassword: Boolean = false,
) {
    ChallengeTogetherTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(end = 0.dp),
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = { if (enabled) onValueChange("") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.Cancel),
                        contentDescription = stringResource(
                            id = R.string.common_designsystem_clear_content_description,
                        ),
                        tint = trailingIconColor
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
        contentAlignment = contentAlignment,
        isPassword = isPassword,
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
                    tint = CustomColorProvider.colorScheme.onSurface,
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.Visibility),
                        contentDescription = "Visibility Icon",
                        tint = CustomColorProvider.colorScheme.onSurface,
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.height(150.dp),
            contentAlignment = Alignment.TopStart,
        )
    }
}
