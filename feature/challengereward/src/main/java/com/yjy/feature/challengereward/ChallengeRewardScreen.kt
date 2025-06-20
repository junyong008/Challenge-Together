package com.yjy.feature.challengereward

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_REWARD_AMOUNT_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_REWARD_NAME_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_REWARD_UNIT_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_REWARD_URL_LENGTH
import com.yjy.common.core.util.formatLargestTimeDuration
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.BaseDialog
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.DialogButtonRow
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.RoundedLinearProgressBar
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.extensions.getDisplayName
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
import com.yjy.feature.challengereward.model.ChallengeRewardUiAction
import com.yjy.feature.challengereward.model.ChallengeRewardUiState
import com.yjy.feature.challengereward.model.RewardInfoUiState
import com.yjy.feature.challengereward.model.isInit
import com.yjy.model.challenge.reward.Reward
import com.yjy.model.challenge.reward.RewardSetting
import com.yjy.model.challenge.reward.RewardUnit
import kotlin.math.roundToLong
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
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun ChallengeRewardScreen(
    modifier: Modifier = Modifier,
    uiState: ChallengeRewardUiState = ChallengeRewardUiState(),
    rewardInfoState: RewardInfoUiState = RewardInfoUiState.Loading,
    processAction: (ChallengeRewardUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    var shouldShowUnitSettingBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowAddRewardBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowRewardSettingBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowEditAmountDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowResetConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var selectedRewardToShowMenu by remember { mutableStateOf<Reward?>(null) }
    var selectedRewardToConfirmEdit by remember { mutableStateOf<Reward?>(null) }
    var selectedRewardToConfirmDelete by remember { mutableStateOf<Reward?>(null) }
    var selectedRewardToConfirmCancelPurchase by remember { mutableStateOf<Reward?>(null) }
    var selectedRewardToPurchase by remember { mutableStateOf<Reward?>(null) }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_challengereward_title,
                rightContent = {
                    if (!rewardInfoState.isInit()) {
                        MenuButton(
                            onClick = { shouldShowRewardSettingBottomSheet = true },
                            modifier = Modifier.padding(end = 4.dp),
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (rewardInfoState.isInit()) {
                ChallengeTogetherButton(
                    onClick = { shouldShowUnitSettingBottomSheet = true },
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
        floatingActionButton = {
            if (!rewardInfoState.isInit()) {
                FloatingActionButton(
                    onClick = { shouldShowAddRewardBottomSheet = true },
                    containerColor = CustomColorProvider.colorScheme.brandDim,
                    contentColor = CustomColorProvider.colorScheme.onBrandDim,
                    shape = CircleShape,
                ) {
                    Icon(
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.Add),
                        contentDescription = stringResource(id = R.string.feature_challengereward_add_reward),
                    )
                }
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->

        when (rewardInfoState) {
            RewardInfoUiState.Error -> {
                ErrorBody(onClickRetry = { processAction(ChallengeRewardUiAction.OnRetryClick) })
            }

            RewardInfoUiState.Loading -> {
                LoadingWheel(
                    modifier = Modifier
                        .padding(padding)
                        .background(CustomColorProvider.colorScheme.background),
                )
            }

            is RewardInfoUiState.Success -> {
                val rewardInfo = rewardInfoState.rewardInfo

                if (shouldShowUnitSettingBottomSheet) {
                    UnitSettingBottomSheet(
                        rewardUnit = rewardInfo?.rewardUnit ?: RewardUnit.Money,
                        amountPerDay = (rewardInfo?.amountPerSec ?: 0.0) * 86400,
                        onConfirm = {
                            shouldShowUnitSettingBottomSheet = false
                        },
                        onDismiss = {
                            shouldShowUnitSettingBottomSheet = false
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
                    if (shouldShowRewardSettingBottomSheet) {
                        RewardSettingMenuBottomSheet(
                            onUnitSettingClick = {
                                shouldShowRewardSettingBottomSheet = false
                                shouldShowUnitSettingBottomSheet = true
                            },
                            onEditAmountClick = {
                                shouldShowEditAmountDialog = true
                            },
                            onResetClick = {
                                shouldShowResetConfirmDialog = true
                            },
                            onDismiss = {
                                shouldShowRewardSettingBottomSheet = false
                            }
                        )
                    }

                    if (shouldShowEditAmountDialog) {
                        EditAmountDialog(
                            amount = rewardInfo.amount,
                            rewardUnit = rewardInfo.rewardUnit,
                            onConfirm = {
                                shouldShowEditAmountDialog = false
                                shouldShowRewardSettingBottomSheet = false
                                processAction(
                                    ChallengeRewardUiAction.OnEditAmountClick(
                                        challengeId = rewardInfo.challengeId,
                                        amount = it,
                                    )
                                )
                            },
                            onDismiss = { shouldShowEditAmountDialog = false },
                        )
                    }

                    if (shouldShowResetConfirmDialog) {
                        ChallengeTogetherDialog(
                            title = stringResource(id = R.string.feature_challengereward_reset),
                            description = stringResource(id = R.string.feature_challengereward_reset_description),
                            positiveTextRes = R.string.feature_challengereward_reset,
                            positiveTextColor = CustomColorProvider.colorScheme.red,
                            onClickPositive = {
                                shouldShowResetConfirmDialog = false
                                shouldShowRewardSettingBottomSheet = false
                                processAction(
                                    ChallengeRewardUiAction.OnResetClick(challengeId = rewardInfo.challengeId)
                                )
                            },
                            onClickNegative = { shouldShowResetConfirmDialog = false },
                        )
                    }

                    if (shouldShowAddRewardBottomSheet) {
                        AddRewardBottomSheet(
                            rewardUnit = rewardInfo.rewardUnit,
                            onConfirm = { name, price, url ->
                                shouldShowAddRewardBottomSheet = false
                                processAction(
                                    ChallengeRewardUiAction.OnAddRewardClick(
                                        name = name,
                                        price = price,
                                        url = url,
                                    )
                                )
                            },
                            onDismiss = {
                                shouldShowAddRewardBottomSheet = false
                            },
                        )
                    }

                    if (selectedRewardToShowMenu != null) {
                        RewardItemMenuBottomSheet(
                            isPurchased = selectedRewardToShowMenu!!.hasPurchased,
                            onEditClick = {
                                selectedRewardToConfirmEdit = selectedRewardToShowMenu
                            },
                            onRestoreClick = {
                                selectedRewardToConfirmCancelPurchase = selectedRewardToShowMenu
                            },
                            onDeleteClick = {
                                selectedRewardToConfirmDelete = selectedRewardToShowMenu
                            },
                            onDismiss = {
                                selectedRewardToShowMenu = null
                            }
                        )
                    }

                    if (selectedRewardToPurchase != null) {
                        ChallengeTogetherDialog(
                            title = stringResource(id = R.string.feature_challengereward_purchase_title),
                            description = stringResource(id = R.string.feature_challengereward_purchase_confirm),
                            positiveTextRes = R.string.feature_challengereward_purchase_title,
                            onClickPositive = {
                                processAction(
                                    ChallengeRewardUiAction.OnPurchaseRewardClick(selectedRewardToPurchase!!)
                                )
                                selectedRewardToPurchase = null
                            },
                            onClickNegative = { selectedRewardToPurchase = null },
                        )
                    }

                    if (selectedRewardToConfirmEdit != null) {
                        EditRewardBottomSheet(
                            reward = selectedRewardToConfirmEdit!!,
                            rewardUnit = rewardInfo.rewardUnit,
                            onConfirm = {
                                selectedRewardToConfirmEdit = null
                                selectedRewardToShowMenu = null
                                processAction(ChallengeRewardUiAction.OnEditRewardClick(it))
                            },
                            onDismiss = {
                                selectedRewardToConfirmEdit = null
                            }
                        )
                    }

                    if (selectedRewardToConfirmCancelPurchase != null) {
                        ChallengeTogetherDialog(
                            title = stringResource(id = R.string.feature_challengereward_restore),
                            description = stringResource(id = R.string.feature_challengereward_cancel_purchase_confirm),
                            positiveTextRes = R.string.feature_challengereward_restore,
                            onClickPositive = {
                                processAction(
                                    ChallengeRewardUiAction.OnCancelPurchaseRewardClick(
                                        selectedRewardToConfirmCancelPurchase!!
                                    )
                                )
                                selectedRewardToConfirmCancelPurchase = null
                                selectedRewardToShowMenu = null
                            },
                            onClickNegative = { selectedRewardToConfirmCancelPurchase = null },
                        )
                    }

                    if (selectedRewardToConfirmDelete != null) {
                        ChallengeTogetherDialog(
                            title = stringResource(id = R.string.feature_challengereward_delete),
                            description = stringResource(id = R.string.feature_challengereward_delete_confirm),
                            positiveTextRes = R.string.feature_challengereward_delete,
                            positiveTextColor = CustomColorProvider.colorScheme.red,
                            onClickPositive = {
                                processAction(
                                    ChallengeRewardUiAction.OnDeleteRewardClick(
                                        selectedRewardToConfirmDelete!!
                                    )
                                )
                                selectedRewardToConfirmDelete = null
                                selectedRewardToShowMenu = null
                            },
                            onClickNegative = { selectedRewardToConfirmDelete = null },
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                    ) {
                        AmountSection(
                            amount = rewardInfo.amount,
                            amountPerSec = rewardInfo.amountPerSec,
                            rewardUnit = rewardInfo.rewardUnit,
                            onEditUnitClick = { shouldShowUnitSettingBottomSheet = true },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (rewardInfo.rewards.isEmpty()) {
                            EmptyRewardBody()
                        } else {
                            Rewards(
                                rewards = rewardInfo.rewards,
                                rewardUnit = rewardInfo.rewardUnit,
                                amount = rewardInfo.amount,
                                amountPerSec = rewardInfo.amountPerSec,
                                onMenuClick = {
                                    selectedRewardToShowMenu = it
                                },
                                onPurchaseClick = {
                                    selectedRewardToPurchase = it
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditAmountDialog(
    amount: Double,
    rewardUnit: RewardUnit,
    onConfirm: (amount: Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var inputAmount by remember { mutableStateOf(amount.toCleanString()) }

    BaseDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.feature_challengereward_edit_total),
        description = stringResource(id = R.string.feature_challengereward_enter_value_to_edit),
        dismissOnClickOutside = false,
    ) {
        SingleLineTextField(
            value = inputAmount,
            onValueChange = {
                val decimalRegex = Regex("^\\d*(\\.\\d*)?$")
                if (it.isEmpty() || it.matches(decimalRegex)) {
                    inputAmount = it.take(MAX_CHALLENGE_REWARD_AMOUNT_LENGTH)
                }
            },
            trailingIcon = {
                if (rewardUnit.getDisplayName().isNotEmpty()) {
                    Text(
                        text = rewardUnit.getDisplayName(),
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            showDefaultClearButton = false,
        )
        Spacer(modifier = Modifier.size(8.dp))
        DialogButtonRow(
            enablePositiveText = (inputAmount.toDoubleOrNull() ?: 0.0) > 0.0,
            onClickNegative = onDismiss,
            onClickPositive = { onConfirm(inputAmount.toDoubleOrNull() ?: 0.0) },
        )
    }
}

@Composable
private fun RewardItemMenuBottomSheet(
    isPurchased: Boolean,
    onEditClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        if (!isPurchased) {
            ClickableText(
                text = stringResource(id = R.string.feature_challengereward_edit),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        } else {
            ClickableText(
                text = stringResource(id = R.string.feature_challengereward_restore),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onRestoreClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        ClickableText(
            text = stringResource(id = R.string.feature_challengereward_delete),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.red,
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RewardSettingMenuBottomSheet(
    onUnitSettingClick: () -> Unit,
    onEditAmountClick: () -> Unit,
    onResetClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_challengereward_edit_unit),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onUnitSettingClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_challengereward_edit_total),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onEditAmountClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_challengereward_reset),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.red,
            onClick = onResetClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
            contentDescription = stringResource(id = R.string.feature_challengereward_reward_menu),
            tint = iconColor,
        )
    }
}

@Composable
private fun Rewards(
    rewards: List<Reward>,
    rewardUnit: RewardUnit,
    amount: Double,
    amountPerSec: Double,
    onMenuClick: (reward: Reward) -> Unit,
    onPurchaseClick: (reward: Reward) -> Unit,
    modifier: Modifier = Modifier,
) {
    val unPurchasedRewards = rewards.filterNot { it.hasPurchased }
    val purchasedRewards = rewards.filter { it.hasPurchased }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (unPurchasedRewards.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.feature_challengereward_target_reward),
                    color = CustomColorProvider.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            items(
                items = unPurchasedRewards,
                key = { it.id },
            ) { reward ->
                RewardItem(
                    reward = reward,
                    rewardUnit = rewardUnit,
                    amount = amount,
                    amountPerSec = amountPerSec,
                    onMenuClick = onMenuClick,
                    onPurchaseClick = onPurchaseClick,
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (purchasedRewards.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.feature_challengereward_achieved_reward),
                    color = CustomColorProvider.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            items(
                items = purchasedRewards,
                key = { it.id },
            ) { reward ->
                RewardItem(
                    reward = reward,
                    rewardUnit = rewardUnit,
                    amount = amount,
                    amountPerSec = amountPerSec,
                    onMenuClick = onMenuClick,
                    onPurchaseClick = onPurchaseClick,
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RewardItem(
    reward: Reward,
    rewardUnit: RewardUnit,
    amount: Double,
    amountPerSec: Double,
    onMenuClick: (reward: Reward) -> Unit,
    onPurchaseClick: (reward: Reward) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val percent = if (amount >= reward.price) {
        100
    } else {
        ((amount / reward.price) * 100).toInt()
    }

    val timeText = if (amount >= reward.price) {
        stringResource(id = R.string.feature_challengereward_available_to_purchase)
    } else {
        val remaining = reward.price - amount
        val secondsLeft = if (amountPerSec > 0) {
            (remaining / amountPerSec).roundToLong()
        } else {
            Long.MAX_VALUE
        }

        stringResource(
            id = R.string.feature_challengereward_remaining_time,
            formatLargestTimeDuration(secondsLeft),
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp, start = 16.dp),
            ) {
                Text(
                    text = reward.name,
                    color = CustomColorProvider.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = rewardUnit.getDisplayName(amount = reward.price),
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            IconButton(
                onClick = { onMenuClick(reward) },
                modifier = Modifier.padding(4.dp),
            ) {
                Icon(
                    ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
                    contentDescription = stringResource(id = R.string.feature_challengereward_reward_menu),
                    tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                )
            }
        }

        if (!reward.hasPurchased) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = timeText,
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoundedLinearProgressBar(
                            progress = { percent / 100f },
                            animated = false,
                            modifier = Modifier
                                .height(12.dp)
                                .weight(1f),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$percent%",
                            color = CustomColorProvider.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(32.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(
                            if (amount >= reward.price) {
                                CustomColorProvider.colorScheme.brandDim
                            } else {
                                CustomColorProvider.colorScheme.disable
                            }
                        )
                        .clickable(
                            onClick = { onPurchaseClick(reward) },
                            enabled = amount >= reward.price,
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_purchase),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (amount >= reward.price) {
                            CustomColorProvider.colorScheme.onBrandDim
                        } else {
                            CustomColorProvider.colorScheme.onDisable
                        },
                        textAlign = TextAlign.Center,
                    )
                }
            }
            if (reward.relateUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(CustomColorProvider.colorScheme.background),
                ) {
                    Icon(
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.Link),
                        contentDescription = stringResource(id = R.string.feature_challengereward_url),
                        tint = CustomColorProvider.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reward.relateUrl,
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.labelSmall.copy(
                            textDecoration = TextDecoration.Underline,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { uriHandler.openUri(reward.relateUrl) }
                            .padding(8.dp)
                            .weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EmptyRewardBody(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
            .background(CustomColorProvider.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.feature_challengereward_no_rewards),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.feature_challengereward_add_reward_tip),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AmountSection(
    amount: Double,
    amountPerSec: Double,
    rewardUnit: RewardUnit,
    onEditUnitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.feature_challengereward_saved_resource),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = rewardUnit.getDisplayName(amount = amount),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        ClickableText(
            text = stringResource(
                id = R.string.feature_challengereward_per_day,
                rewardUnit.getDisplayName(amount = amountPerSec * 86400),
            ),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            onClick = { onEditUnitClick() },
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.End),
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun EditRewardBottomSheet(
    reward: Reward,
    rewardUnit: RewardUnit,
    onConfirm: (reward: Reward) -> Unit,
    onDismiss: () -> Unit,
) {
    var inputName by remember { mutableStateOf(reward.name) }
    var inputPrice by remember { mutableStateOf(reward.price.toCleanString()) }
    var inputUrl by remember { mutableStateOf(reward.relateUrl) }

    BaseBottomSheet(
        onDismiss = onDismiss,
        disableDragToDismiss = true,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.feature_challengereward_edit),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.feature_challengereward_enter_reward_info),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))
        SingleLineTextField(
            value = inputName,
            onValueChange = {
                inputName = it.take(MAX_CHALLENGE_REWARD_NAME_LENGTH)
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_name),
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                    )
                }
            },
            trailingIconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleLineTextField(
            value = inputPrice,
            onValueChange = {
                val decimalRegex = Regex("^\\d*(\\.\\d*)?$")
                if (it.isEmpty() || it.matches(decimalRegex)) {
                    inputPrice = it.take(MAX_CHALLENGE_REWARD_AMOUNT_LENGTH)
                }
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_price),
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                    )
                }
            },
            trailingIcon = {
                if (rewardUnit.getDisplayName().isNotEmpty()) {
                    Text(
                        text = rewardUnit.getDisplayName(),
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            showDefaultClearButton = false,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        SingleLineTextField(
            value = inputUrl,
            onValueChange = {
                inputUrl = it.take(MAX_CHALLENGE_REWARD_URL_LENGTH)
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_url),
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                    )
                }
            },
            trailingIconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            placeholderText = stringResource(id = R.string.feature_challengereward_optional),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            placeholderColor = CustomColorProvider.colorScheme.onBackground.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChallengeTogetherButton(
            onClick = {
                val price = inputPrice.toDoubleOrNull() ?: 0.0
                val modifiedReward = reward.copy(
                    name = inputName,
                    price = price,
                    relateUrl = inputUrl,
                )
                onConfirm(modifiedReward)
            },
            enabled = (inputPrice.toDoubleOrNull() ?: 0.0) > 0.0 && inputName.isNotBlank(),
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
private fun AddRewardBottomSheet(
    rewardUnit: RewardUnit,
    onConfirm: (name: String, price: Double, url: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var inputName by remember { mutableStateOf("") }
    var inputPrice by remember { mutableStateOf("") }
    var inputUrl by remember { mutableStateOf("") }

    BaseBottomSheet(
        onDismiss = onDismiss,
        disableDragToDismiss = true,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.feature_challengereward_add_reward),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.feature_challengereward_enter_reward_info),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))
        SingleLineTextField(
            value = inputName,
            onValueChange = {
                inputName = it.take(MAX_CHALLENGE_REWARD_NAME_LENGTH)
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_name),
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                    )
                }
            },
            trailingIconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleLineTextField(
            value = inputPrice,
            onValueChange = {
                val decimalRegex = Regex("^\\d*(\\.\\d*)?$")
                if (it.isEmpty() || it.matches(decimalRegex)) {
                    inputPrice = it.take(MAX_CHALLENGE_REWARD_AMOUNT_LENGTH)
                }
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_price),
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                    )
                }
            },
            trailingIcon = {
                if (rewardUnit.getDisplayName().isNotEmpty()) {
                    Text(
                        text = rewardUnit.getDisplayName(),
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            showDefaultClearButton = false,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        SingleLineTextField(
            value = inputUrl,
            onValueChange = {
                inputUrl = it.take(MAX_CHALLENGE_REWARD_URL_LENGTH)
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.feature_challengereward_url),
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    VerticalDivider(
                        thickness = 2.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                    )
                }
            },
            trailingIconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done,
            ),
            placeholderText = stringResource(id = R.string.feature_challengereward_optional),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            placeholderColor = CustomColorProvider.colorScheme.onBackground.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChallengeTogetherButton(
            onClick = {
                val price = inputPrice.toDoubleOrNull() ?: 0.0
                onConfirm(inputName, price, inputUrl)
            },
            enabled = (inputPrice.toDoubleOrNull() ?: 0.0) > 0.0 && inputName.isNotBlank(),
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

private enum class HabitPeriod {
    DAILY, WEEKLY, MONTHLY;

    fun toSeconds(): Long = when (this) {
        DAILY -> 86400L
        WEEKLY -> 604800L
        MONTHLY -> 2592000L
    }
}

@Composable
private fun UnitSettingBottomSheet(
    rewardUnit: RewardUnit,
    amountPerDay: Double,
    onConfirm: (rewardSetting: RewardSetting) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedRewardUnit by remember { mutableStateOf(rewardUnit) }
    var selectedHabitPeriod by remember { mutableStateOf(HabitPeriod.DAILY) }
    var inputAmount by remember { mutableStateOf(amountPerDay.toCleanString()) }

    val saveAmountPerDay = (inputAmount.toDoubleOrNull() ?: 0.0) / selectedHabitPeriod.toSeconds() * 86400

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
                    inputAmount = it.take(MAX_CHALLENGE_REWARD_AMOUNT_LENGTH)
                }
            },
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            trailingIcon = {
                if (selectedRewardUnit.getDisplayName().isNotEmpty()) {
                    Text(
                        text = selectedRewardUnit.getDisplayName(),
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            textAlign = TextAlign.Center,
            contentAlignment = Alignment.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                id = R.string.feature_challengereward_example_description,
                selectedRewardUnit.getDisplayName(amount = saveAmountPerDay, maxFractionDigits = 1),
            ),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.align(Alignment.End),
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
            enabled = (inputAmount.toDoubleOrNull() ?: 0.0) > 0.0,
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
                    onRewardUnitChange(RewardUnit.Custom(unit = it.take(MAX_CHALLENGE_REWARD_UNIT_LENGTH)))
                },
                leadingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.feature_challengereward_unit_label),
                            color = CustomColorProvider.colorScheme.onSurfaceMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        VerticalDivider(
                            thickness = 2.dp,
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

private fun Double.toCleanString(): String {
    return this.toLong().toString()
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
