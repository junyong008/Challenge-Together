package com.yjy.core.designsystem.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.theme.CustomColorProvider

@Composable
fun ClickableText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = CustomColorProvider.colorScheme.onBackground,
    style: TextStyle = MaterialTheme.typography.labelSmall,
    textAlign: TextAlign = TextAlign.Start,
) {
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    Text(
        text = text,
        color = color,
        style = style.copy(
            textDecoration = TextDecoration.Underline
        ),
        textAlign = textAlign,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            )
            .padding(8.dp)
    )
}