package com.yjy.feature.notificationsetting

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherSwitch
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.model.common.notification.NotificationSettingFlags

@Composable
internal fun NotificationSettingRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingViewModel = hiltViewModel(),
) {
    val settings by viewModel.notificationSettings.collectAsStateWithLifecycle()

    NotificationSettingScreen(
        modifier = modifier,
        settings = settings,
        setNotificationSetting = viewModel::setNotificationSetting,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun NotificationSettingScreen(
    modifier: Modifier = Modifier,
    settings: Int = 0xFF,
    setNotificationSetting: (Int, Boolean) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
) {
    val isAllEnabled = NotificationSettingFlags.isEnabled(settings, NotificationSettingFlags.ALL)

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.feature_notification_setting_all),
                    style = MaterialTheme.typography.titleMedium,
                    color = CustomColorProvider.colorScheme.onBackground,
                    modifier = Modifier.weight(1f),
                )
                ChallengeTogetherSwitch(
                    contentColor = CustomColorProvider.colorScheme.background,
                    containerColor = CustomColorProvider.colorScheme.surface,
                    checked = isAllEnabled,
                    onCheckedChange = { setNotificationSetting(NotificationSettingFlags.ALL, it) },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = CustomColorProvider.colorScheme.divider,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.feature_notification_setting_challenge),
                    style = MaterialTheme.typography.titleSmall,
                    color = CustomColorProvider.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_goal_achieved,
                    descriptionResId = R.string.feature_notification_setting_goal_achieved_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.GOAL_ACHIEVED,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.GOAL_ACHIEVED, it)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_waiting_room_participants,
                    descriptionResId = R.string.feature_notification_setting_waiting_room_participants_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.WAITING_ROOM_PARTICIPANTS,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.WAITING_ROOM_PARTICIPANTS, it)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_waiting_room_updates,
                    descriptionResId = R.string.feature_notification_setting_waiting_room_updates_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.WAITING_ROOM_UPDATE,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.WAITING_ROOM_UPDATE, it)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_challenge_room_progress,
                    descriptionResId = R.string.feature_notification_setting_challenge_room_progress_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.CHALLENGE_ROOM_PROGRESS,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.CHALLENGE_ROOM_PROGRESS, it)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_challenge_board,
                    descriptionResId = R.string.feature_notification_setting_challenge_board_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.CHALLENGE_BOARD,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.CHALLENGE_BOARD, it)
                    },
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.feature_notification_setting_community),
                    style = MaterialTheme.typography.titleSmall,
                    color = CustomColorProvider.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_comment,
                    descriptionResId = R.string.feature_notification_setting_comment_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.COMMUNITY_COMMENT,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.COMMUNITY_COMMENT, it)
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                NotificationSettingItem(
                    titleResId = R.string.feature_notification_setting_reply,
                    descriptionResId = R.string.feature_notification_setting_reply_detail,
                    enabled = isAllEnabled,
                    checked = NotificationSettingFlags.isEnabled(
                        settings = settings,
                        flag = NotificationSettingFlags.COMMUNITY_REPLY,
                    ),
                    onCheckedChange = {
                        setNotificationSetting(NotificationSettingFlags.COMMUNITY_REPLY, it)
                    },
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private const val ITEM_SWITCH_SCALE = 0.8f

@Composable
private fun NotificationSettingItem(
    @StringRes titleResId: Int,
    @StringRes descriptionResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 85.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = titleResId),
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = descriptionResId),
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        ChallengeTogetherSwitch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .scale(ITEM_SWITCH_SCALE)
                .align(Alignment.CenterVertically),
        )
    }
}

@DevicePreviews
@Composable
fun NotificationSettingScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            NotificationSettingScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}