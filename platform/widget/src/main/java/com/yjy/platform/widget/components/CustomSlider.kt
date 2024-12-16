package com.yjy.platform.widget.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.theme.CustomColorProvider
import kotlin.math.roundToInt

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumbSize: Dp = 16.dp,
    trackHeight: Dp = 4.dp,
    thumbColor: Color = CustomColorProvider.colorScheme.brandDim,
    trackColor: Color = CustomColorProvider.colorScheme.divider,
    progressColor: Color = CustomColorProvider.colorScheme.brand,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newValue = (offset.x / size.width).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val newValue = (change.position.x / size.width).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(trackColor),
        )

        // Progress
        Box(
            modifier = Modifier
                .fillMaxWidth(value)
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(progressColor),
        )

        // Thumb
        Box(
            modifier = Modifier
                .offset {
                    val offsetX = (value * (maxWidth.toPx() - thumbSize.toPx()))
                    IntOffset(x = offsetX.roundToInt(), y = 0)
                }
                .size(thumbSize)
                .clip(CircleShape)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .background(thumbColor),
        )
    }
}
