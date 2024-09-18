package com.yjy.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons

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
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    placeholderColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
    borderColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    shouldHidePassword: Boolean = false,
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .background(backgroundColor, shape = shape)
            .border(1.dp, borderColor, shape = shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (leadingIcon != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                leadingIcon()
            }
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = textColor),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = if (shouldHidePassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (leadingIcon != null) 36.dp else 0.dp),
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodySmall.copy(color = placeholderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                )
            }
            innerTextField()
        }

        if (trailingIcon != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                trailingIcon()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChallengeTogetherTextFieldPreview() {
    ChallengeTogetherTextField(
        value = "",
        onValueChange = {},
        placeholderText = "Enter text...",
        leadingIcon = {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Mail),
                contentDescription = "Email Icon"
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Visibility),
                contentDescription = "Visibility Icon"
            )
        },
        singleLine = true,
        borderColor = Color.Gray,
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}
