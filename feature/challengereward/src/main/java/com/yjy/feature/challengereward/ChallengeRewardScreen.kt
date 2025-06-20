package com.yjy.feature.challengereward

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.extensions.getDisplayName
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
import com.yjy.feature.challengereward.model.ChallengeRewardUiState
import com.yjy.feature.challengereward.model.RewardInfoUiState
import com.yjy.feature.challengereward.model.getRewardInfo
import com.yjy.feature.challengereward.model.isSuccess
import com.yjy.model.challenge.reward.RewardSetting
import com.yjy.model.challenge.reward.RewardUnit
import com.yjy.common.designsystem.R as designSystemR

@Composable
internal fun ChallengeRewardRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeRewardViewModel = hiltViewModel(),
) {
    val rewardInfoState by viewModel.rewardInfoState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChallengeRewardScreen(
        modifier = modifier,
        uiState = uiState,
        rewardInfoState = rewardInfoState,
        retryOnError = viewModel::retryOnError,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun ChallengeRewardScreen(
    modifier: Modifier = Modifier,
    uiState: ChallengeRewardUiState = ChallengeRewardUiState(),
    rewardInfoState: RewardInfoUiState = RewardInfoUiState.Loading,
    retryOnError: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    var shouldShowUnitSettingDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_challengereward_title,
            )
        },
        bottomBar = {
            if (rewardInfoState.isSuccess() && rewardInfoState.getRewardInfo() == null) {
                ChallengeTogetherButton(
                    onClick = { shouldShowUnitSettingDialog = true },
                    enabled = !uiState.isSettingInitUnit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    if (uiState.isSettingInitUnit) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = CustomColorProvider.colorScheme.brand,
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.feature_challengereward_start_tracking),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->

        when (rewardInfoState) {
            RewardInfoUiState.Error -> ErrorBody(onClickRetry = retryOnError)
            RewardInfoUiState.Loading -> {
                LoadingWheel(
                    modifier = Modifier
                        .padding(padding)
                        .background(CustomColorProvider.colorScheme.background),
                )
            }

            is RewardInfoUiState.Success -> {
                val rewardInfo = rewardInfoState.rewardInfo

                if (shouldShowUnitSettingDialog) {
                    UnitSettingBottomSheet(
                        initRewardUnit = rewardInfo?.rewardUnit ?: RewardUnit.Money,
                        onConfirm = {
                            shouldShowUnitSettingDialog = false
                        },
                        onDismiss = {
                            shouldShowUnitSettingDialog = false
                        }
                    )
                }

                if (rewardInfo == null) {
                    InitBody(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 32.dp),
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {

                    }
                }
            }
        }
    }
}

private enum class HabitPeriod {
    DAILY, WEEKLY, MONTHLY;

    fun toSeconds(): Long = when (this) {
        HabitPeriod.DAILY -> 86400L
        HabitPeriod.WEEKLY -> 604800L
        HabitPeriod.MONTHLY -> 2592000L
    }
}

@Composable
private fun UnitSettingBottomSheet(
    initRewardUnit: RewardUnit,
    onConfirm: (rewardSetting: RewardSetting) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRewardUnit by remember { mutableStateOf(initRewardUnit) }
    var selectedHabitPeriod by remember { mutableStateOf(HabitPeriod.DAILY) }
    var inputAmount by remember { mutableStateOf("") }

    BaseBottomSheet(
        onDismiss = onDismiss,
        disableDragToDismiss = true,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.feature_challengereward_unit_setting_title),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.feature_challengereward_unit_setting_description),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))
        UnitItems(
            rewardUnit = selectedRewardUnit,
            onRewardUnitChange = { selectedRewardUnit = it },
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        TripleSwitch(
            labels = listOf(
                stringResource(id = R.string.feature_challengereward_period_daily),
                stringResource(id = R.string.feature_challengereward_period_weekly),
                stringResource(id = R.string.feature_challengereward_period_monthly),
            ),
            selectedIndex = selectedHabitPeriod.ordinal,
            onSwitchChange = { selectedHabitPeriod = HabitPeriod.entries.toTypedArray()[it] },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleLineTextField(
            value = inputAmount,
            onValueChange = {
                val decimalRegex = Regex("^\\d*(\\.\\d*)?$")
                if (it.isEmpty() || it.matches(decimalRegex)) {
                    inputAmount = it
                }
            },
            backgroundColor = CustomColorProvider.colorScheme.background,
            trailingIcon = {
                Text(
                    text = selectedRewardUnit.getDisplayName(),
                    color = CustomColorProvider.colorScheme.onBackgroundMuted,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            textAlign = TextAlign.Center,
            contentAlignment = Alignment.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChallengeTogetherButton(
            onClick = {
                val amount = inputAmount.toDoubleOrNull() ?: 0.0
                val seconds = selectedHabitPeriod.toSeconds()
                val rewardPerSecond = if (seconds > 0) amount / seconds else 0.0
                val rewardSetting = RewardSetting(valuePerSec = rewardPerSecond, unit = selectedRewardUnit)
                onConfirm(rewardSetting)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.feature_challengereward_confirm),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TripleSwitch(
    labels: List<String>,
    selectedIndex: Int,
    onSwitchChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    switchBackgroundColor: Color = CustomColorProvider.colorScheme.background,
    selectedColor: Color = CustomColorProvider.colorScheme.surface,
    onSelectedColor: Color = CustomColorProvider.colorScheme.onSurface,
    unselectedColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
    selectedTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    unSelectedTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    backgroundShape: Shape = MaterialTheme.shapes.medium,
    selectedShape: Shape = MaterialTheme.shapes.small,
    gap: Dp = 4.dp,
) {
    require(labels.size == 3) { "TripleSwitch requires exactly 3 labels" }

    val density = LocalDensity.current
    val widths = remember { mutableStateListOf(0, 0, 0) }

    val offsetX by animateDpAsState(
        targetValue = widths.take(selectedIndex).sum().dp,
        label = "TripleSwitchOffset",
    )

    val selectedWidth by animateDpAsState(
        targetValue = widths.getOrNull(selectedIndex)?.dp ?: 0.dp,
        label = "TripleSwitchWidth",
    )

    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(backgroundShape)
            .background(if (enabled) switchBackgroundColor else CustomColorProvider.colorScheme.disable)
            .padding(gap),
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToPx(), 0) }
                .width(selectedWidth)
                .fillMaxHeight()
                .clip(selectedShape)
                .background(selectedColor),
        )

        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            labels.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            enabled = enabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSwitchChange(index) }
                        .onGloballyPositioned { coordinates ->
                            val px = with(density) {
                                coordinates.size.width.toDp().value
                                    .takeIf { it.isFinite() }
                                    ?.toInt()
                                    ?.coerceAtLeast(0)
                                    ?: 0
                            }
                            if (index < widths.size) {
                                widths[index] = px
                            }
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        color = when {
                            !enabled -> CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.38f)
                            selectedIndex == index -> onSelectedColor
                            else -> unselectedColor
                        },
                        style = if (selectedIndex == index) selectedTextStyle else unSelectedTextStyle,
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitItems(
    rewardUnit: RewardUnit,
    onRewardUnitChange: (RewardUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.background)
            .animateContentSize(),
    ) {
        UnitItem(
            title = stringResource(R.string.feature_challengereward_unit_money),
            isSelected = rewardUnit == RewardUnit.Money,
            onClick = { onRewardUnitChange(RewardUnit.Money) },
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        UnitItem(
            title = stringResource(R.string.feature_challengereward_unit_time),
            isSelected = rewardUnit == RewardUnit.Time,
            onClick = { onRewardUnitChange(RewardUnit.Time) },
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        UnitItem(
            title = stringResource(R.string.feature_challengereward_unit_custom),
            isSelected = rewardUnit is RewardUnit.Custom,
            onClick = { onRewardUnitChange(RewardUnit.Custom(unit = "")) },
        )
        if (rewardUnit is RewardUnit.Custom) {
            SingleLineTextField(
                value = rewardUnit.unit,
                onValueChange = {
                    onRewardUnitChange(RewardUnit.Custom(unit = it))
                },
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.feature_challengereward_unit_label),
                            color = CustomColorProvider.colorScheme.onSurfaceMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        VerticalDivider(
                            thickness = 1.dp,
                            color = CustomColorProvider.colorScheme.divider,
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                        )
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(180.dp)
                    .align(Alignment.End),
                showDefaultClearButton = false,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.feature_challengereward_unit_custom_hint),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UnitItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clip(shape)
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = CustomColorProvider.colorScheme.brand,
                unselectedColor = CustomColorProvider.colorScheme.onBackgroundMuted,
            ),
        )
    }
}

@Composable
private fun InitBody(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StableImage(
                drawableResId = designSystemR.drawable.image_money,
                descriptionResId = R.string.feature_challengereward_start_title,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(150.dp),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.feature_challengereward_start_title),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.feature_challengereward_start_description),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@DevicePreviews
@Composable
fun ChallengeRewardScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ChallengeRewardScreen(
            )
        }
    }
}
