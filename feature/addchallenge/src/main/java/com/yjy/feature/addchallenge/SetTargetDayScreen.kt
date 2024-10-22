package com.yjy.feature.addchallenge

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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst.INIT_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_TARGET_DAYS
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.CursorLessNumberTextField
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.feature.addchallenge.navigation.AddChallengeStrings
import com.yjy.model.challenge.TargetDays

@Composable
internal fun SetTargetDayRoute(
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetTargetDayScreen(
        modifier = modifier,
        uiState = uiState,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onContinue = onContinue,
    )
}

@Composable
internal fun SetTargetDayScreen(
    modifier: Modifier = Modifier,
    uiState: AddChallengeUiState = AddChallengeUiState(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinue: () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            ChallengeTogetherBottomAppBar(
                onBackClick = onBackClick,
                onContinueClick = onContinue,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TitleWithDescription(
                titleRes = AddChallengeStrings.feature_addchallenge_title_set_target_day,
                descriptionRes = AddChallengeStrings.feature_addchallenge_description_set_target_day,
            )
            Spacer(modifier = Modifier.height(100.dp))
            TargetDaysSelector(
                targetDays = uiState.targetDays,
                minDays = MIN_CHALLENGE_TARGET_DAYS,
                maxDays = MAX_CHALLENGE_TARGET_DAYS,
                onTargetDaysUpdated = { processAction(AddChallengeUiAction.OnTargetDaysUpdated(it)) },
                containerColor = CustomColorProvider.colorScheme.surface,
                contentColor = CustomColorProvider.colorScheme.onSurface,
                shape = MaterialTheme.shapes.large,
                textStyle = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            TargetDaySwitch(
                leftText = stringResource(id = AddChallengeStrings.feature_addchallenge_target_day),
                rightText = stringResource(id = AddChallengeStrings.feature_addchallenge_unlimited),
                isLeftSelected = uiState.targetDays is TargetDays.Fixed,
                onSwitchChange = { isLeftSelected ->
                    processAction(
                        AddChallengeUiAction.OnTargetDaysUpdated(
                            if (isLeftSelected) {
                                TargetDays.Fixed(INIT_CHALLENGE_TARGET_DAYS)
                            } else {
                                TargetDays.Infinite
                            }
                        )
                    )
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
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
                        painter = painterResource(id = ChallengeTogetherIcons.Infinite),
                        tint = CustomColorProvider.colorScheme.onBackground,
                        contentDescription = stringResource(
                            id = AddChallengeStrings.feature_addchallenge_unlimited
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
            text = stringResource(id = AddChallengeStrings.feature_addchallenge_days),
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
    switchBackgroundColor: Color = CustomColorProvider.colorScheme.surface,
    selectedColor: Color = CustomColorProvider.colorScheme.brand,
    onSelectedColor: Color = CustomColorProvider.colorScheme.onBrand,
    unselectedColor: Color = CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.3f),
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
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
        label = "SwitchWidth Animation"
    )

    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(switchBackgroundColor)
            .padding(gap)
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToPx(), 0) }
                .width(animatedWidth)
                .fillMaxHeight()
                .clip(shape)
                .background(selectedColor)
        )

        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSwitchChange(true) }
                    .onGloballyPositioned { coordinates ->
                        leftWidth = with(density) { coordinates.size.width.toDp().value.toInt() }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = leftText,
                    textAlign = TextAlign.Center,
                    color = if (isLeftSelected) onSelectedColor else unselectedColor,
                    style = textStyle,
                )

            }
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSwitchChange(false) }
                    .onGloballyPositioned { coordinates ->
                        rightWidth = with(density) { coordinates.size.width.toDp().value.toInt() }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = rightText,
                    textAlign = TextAlign.Center,
                    color = if (isLeftSelected) unselectedColor else onSelectedColor,
                    style = textStyle,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun SetTargetDayScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SetTargetDayScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
