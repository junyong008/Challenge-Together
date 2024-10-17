package com.yjy.common.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = CustomColorProvider.colorScheme.background,
    contentColor: Color = CustomColorProvider.colorScheme.surface,
    accentColor: Color = CustomColorProvider.colorScheme.brandDim,
    disableColor: Color = CustomColorProvider.colorScheme.disable,
    onDisableColor: Color = CustomColorProvider.colorScheme.onDisable,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = contentColor,
            uncheckedThumbColor = accentColor,
            checkedTrackColor = accentColor,
            uncheckedTrackColor = containerColor,
            checkedBorderColor = accentColor,
            uncheckedBorderColor = containerColor,
            checkedIconColor = contentColor,
            uncheckedIconColor = accentColor,

            disabledCheckedThumbColor = contentColor,
            disabledUncheckedThumbColor = disableColor,
            disabledCheckedTrackColor = disableColor,
            disabledUncheckedTrackColor = containerColor,
            disabledCheckedBorderColor = disableColor,
            disabledUncheckedBorderColor = containerColor,
            disabledCheckedIconColor = onDisableColor,
            disabledUncheckedIconColor = disableColor,
        )
    )
}

// 리플 효과 없애기 위해 커스텀 예정
@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = CustomColorProvider.colorScheme.background,
    contentColor: Color = CustomColorProvider.colorScheme.surface,
    accentColor: Color = CustomColorProvider.colorScheme.brandDim,
    disableColor: Color = CustomColorProvider.colorScheme.disable,
    onDisableColor: Color = CustomColorProvider.colorScheme.onDisable,
    thumbSize: Dp = 20.dp,
    switchWidth: Dp = 40.dp,
    switchHeight: Dp = 24.dp,
    padding: Dp = 4.dp
) {
    val thumbPosition by animateDpAsState(
        targetValue = if (checked) switchWidth - thumbSize - padding else padding,
        label = "Thumb Position Animation"
    )

    Box(
        modifier = modifier
            .size(switchWidth, switchHeight)
            .clip(RoundedCornerShape(50))
            .background(if (enabled) accentColor else disableColor)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCheckedChange(!checked)
            }
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbPosition)
                .size(thumbSize)
                .clip(CircleShape)
                .background(if (enabled) contentColor else onDisableColor)
        )
    }
}


@ComponentPreviews
@Composable
fun CustomSwitchPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherSwitch(checked = true, onCheckedChange = {})
    }
}
