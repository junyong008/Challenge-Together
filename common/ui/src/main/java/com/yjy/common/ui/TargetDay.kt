package com.yjy.common.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.yjy.common.core.constants.ChallengeConst.INIT_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_TARGET_DAYS
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.component.CursorLessNumberTextField
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.core.TargetDays

@Composable
fun TargetDay(
    targetDays: TargetDays,
    onTargetDaysUpdated: (TargetDays) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TargetDaysSelector(
            targetDays = targetDays,
            minDays = MIN_CHALLENGE_TARGET_DAYS,
            maxDays = MAX_CHALLENGE_TARGET_DAYS,
            onTargetDaysUpdated = onTargetDaysUpdated,
            containerColor = CustomColorProvider.colorScheme.surface,
            contentColor = CustomColorProvider.colorScheme.onSurface,
            shape = MaterialTheme.shapes.extraLarge,
            textStyle = MaterialTheme.typography.displaySmall,
            enabled = enabled,
        )

        Spacer(modifier = Modifier.height(20.dp))

        TargetDaySwitch(
            leftText = stringResource(id = R.string.common_ui_target_day),
            rightText = stringResource(id = R.string.common_ui_target_day_unlimited),
            isLeftSelected = targetDays is TargetDays.Fixed,
            onSwitchChange = { isLeftSelected ->
                onTargetDaysUpdated(
                    if (isLeftSelected) {
                        TargetDays.Fixed(INIT_CHALLENGE_TARGET_DAYS)
                    } else {
                        TargetDays.Infinite
                    },
                )
            },
            enabled = enabled,
        )
    }
}

@Composable
private fun TargetDaysSelector(
    targetDays: TargetDays,
    minDays: Int,
    maxDays: Int,
    onTargetDaysUpdated: (TargetDays) -> Unit,
    containerColor: Color,
    contentColor: Color,
    shape: Shape,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    width: Dp = 140.dp,
    height: Dp = 80.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (targetDays) {
            is TargetDays.Fixed -> {
                CursorLessNumberTextField(
                    value = targetDays.days,
                    onValueChange = { onTargetDaysUpdated(TargetDays.Fixed(it)) },
                    minLimit = minDays,
                    maxLimit = maxDays,
                    shape = shape,
                    enabled = enabled,
                    textBackground = containerColor,
                    textColor = contentColor,
                    textStyle = textStyle,
                    modifier = Modifier.size(width = width, height = height),
                )
            }

            TargetDays.Infinite -> {
                Box(
                    modifier = Modifier.size(width = width, height = height),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.Infinite),
                        tint = CustomColorProvider.colorScheme.onBackground,
                        contentDescription = stringResource(
                            id = R.string.common_ui_target_day_unlimited,
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = R.string.common_ui_target_day_days),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Composable
private fun TargetDaySwitch(
    leftText: String,
    rightText: String,
    isLeftSelected: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    switchBackgroundColor: Color = CustomColorProvider.colorScheme.surface,
    selectedColor: Color = CustomColorProvider.colorScheme.brand,
    onSelectedColor: Color = CustomColorProvider.colorScheme.onBrand,
    unselectedColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
    selectedTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    unSelectedTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    gap: Dp = 6.dp,
) {
    val density = LocalDensity.current

    var leftWidth by remember { mutableIntStateOf(0) }
    var rightWidth by remember { mutableIntStateOf(0) }

    val offsetX by animateDpAsState(
        targetValue = if (isLeftSelected) 0.dp else leftWidth.dp,
        label = "SwitchOffset Animation",
    )

    val animatedWidth by animateDpAsState(
        targetValue = if (isLeftSelected) leftWidth.dp else rightWidth.dp,
        label = "SwitchWidth Animation",
    )

    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(if (enabled) switchBackgroundColor else CustomColorProvider.colorScheme.disable)
            .padding(gap),
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToPx(), 0) }
                .width(animatedWidth)
                .fillMaxHeight()
                .clip(shape)
                .background(selectedColor),
        )

        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        enabled = enabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSwitchChange(true) }
                    .onGloballyPositioned { coordinates ->
                        leftWidth = with(density) {
                            coordinates.size.width.toDp().value
                                .takeIf { it.isFinite() }
                                ?.toInt()
                                ?.coerceAtLeast(0)
                                ?: 0
                        }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = leftText,
                    textAlign = TextAlign.Center,
                    color = when {
                        !enabled -> CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.38f)
                        isLeftSelected -> onSelectedColor
                        else -> unselectedColor
                    },
                    style = if (isLeftSelected) selectedTextStyle else unSelectedTextStyle,
                )
            }
            Box(
                modifier = Modifier
                    .clickable(
                        enabled = enabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSwitchChange(false) }
                    .onGloballyPositioned { coordinates ->
                        rightWidth = with(density) {
                            coordinates.size.width.toDp().value
                                .takeIf { it.isFinite() }
                                ?.toInt()
                                ?.coerceAtLeast(0)
                                ?: 0
                        }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = rightText,
                    textAlign = TextAlign.Center,
                    color = when {
                        !enabled -> CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.38f)
                        isLeftSelected -> unselectedColor
                        else -> onSelectedColor
                    },
                    style = if (isLeftSelected) unSelectedTextStyle else selectedTextStyle,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
fun TargetDayPreview() {
    ChallengeTogetherTheme {
        TargetDay(
            targetDays = TargetDays.Infinite,
            onTargetDaysUpdated = {},
        )
    }
}
