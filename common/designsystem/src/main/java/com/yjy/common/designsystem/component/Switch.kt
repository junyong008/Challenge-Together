package com.yjy.common.designsystem.component

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent,
            checkedIconColor = contentColor,
            uncheckedIconColor = accentColor,

            disabledCheckedThumbColor = contentColor,
            disabledUncheckedThumbColor = disableColor,
            disabledCheckedTrackColor = disableColor,
            disabledUncheckedTrackColor = containerColor,
            disabledCheckedBorderColor = Color.Transparent,
            disabledUncheckedBorderColor = Color.Transparent,
            disabledCheckedIconColor = onDisableColor,
            disabledUncheckedIconColor = disableColor,
        )
    )
}

@ComponentPreviews
@Composable
fun CustomSwitchPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherSwitch(checked = false, enabled = false, onCheckedChange = {})
    }
}
