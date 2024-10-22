package com.yjy.common.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
    enabled: Boolean = true,
    contentAlignment: Alignment = Alignment.CenterStart,
    isPassword: Boolean = false,
    passwordIconColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
) {
    var passwordVisible by remember { mutableStateOf(false) }

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
            modifier = modifier
                .background(
                    color = if (enabled) {
                        backgroundColor
                    } else {
                        CustomColorProvider.colorScheme.disable
                    },
                    shape = shape,
                )
                .heightIn(min = 50.dp)
                .height(IntrinsicSize.Min),
        ) { innerTextField ->

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(
                    contentAlignment = contentAlignment,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 14.dp),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = textStyle.copy(color = placeholderColor, textAlign = textAlign),
                        )
                    }
                    innerTextField()
                }

                // 비밀번호 입력 칸이면 기본적으로 보기 / 가리기 기능 제공.
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

                // trailingIcon은 주로 IconButton이 위치하므로 leadingIcon과 padding을 달리함.
                if (trailingIcon != null) {
                    trailingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Spacer(modifier = Modifier.width(16.dp))
                }
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
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
    contentAlignment: Alignment = Alignment.CenterStart,
    passwordIconColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
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
        backgroundColor = backgroundColor,
        contentAlignment = contentAlignment,
        passwordIconColor = passwordIconColor,
        isPassword = isPassword,
    )
}

@Composable
fun CursorLessNumberTextField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minLimit: Int = 0,
    maxLimit: Int = 99,
    textBackground: Color = CustomColorProvider.colorScheme.surface,
    textColor: Color = CustomColorProvider.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    val maxLength = maxLimit.toString().length

    var internalValue by remember { mutableStateOf(value.toString()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = internalValue,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }.take(maxLength)
            internalValue = filteredValue

            filteredValue.toIntOrNull()?.let {
                val newValueWithinLimit = it.coerceIn(minLimit, maxLimit)
                internalValue = newValueWithinLimit.toString()
                onValueChange(newValueWithinLimit)
            }
        },
        decorationBox = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(shape)
                    .background(textBackground)
                    .clickable {
                        internalValue = ""
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
            ) {
                Text(
                    text = value.toString(),
                    style = textStyle,
                    color = if (internalValue.isEmpty()) {
                        textColor.copy(alpha = 0.3f)
                    } else {
                        textColor
                    },
                    textAlign = TextAlign.Center,
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        modifier = modifier.focusRequester(focusRequester),
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
