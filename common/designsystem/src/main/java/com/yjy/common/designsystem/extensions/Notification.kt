package com.yjy.common.designsystem.extensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yjy.common.designsystem.R
import com.yjy.model.common.notification.NotificationType

private val notificationFormats = mapOf(
    NotificationType.CHALLENGE_RESET to Pair(
        R.string.common_designsystem_notification_participant_reset,
        R.string.common_designsystem_notification_user_reset,
    ),
    NotificationType.CHALLENGE_DELETE to Pair(
        R.string.common_designsystem_notification_participant_give_up,
        R.string.common_designsystem_notification_user_give_up,
    ),
    NotificationType.CHALLENGE_STARTED_POST to Pair(
        R.string.common_designsystem_notification_challengeboard_post_added,
        null,
    ),
    NotificationType.CHALLENGE_FORCE_REMOVE to Pair(
        R.string.common_designsystem_notification_participant_force_remove,
        R.string.common_designsystem_notification_user_force_remove,
    ),
)

@Composable
fun NotificationType.formatMessage(header: String, body: String): Pair<String, String> {
    val (titleResId, bodyResId) = notificationFormats[this] ?: return Pair(header, body)
    return Pair(
        stringResource(titleResId, header),
        bodyResId?.let { stringResource(it, body) } ?: body,
    )
}

fun NotificationType.formatMessageWithContext(
    context: Context,
    header: String,
    body: String,
): Pair<String, String> {
    val (titleResId, bodyResId) = notificationFormats[this] ?: return Pair(header, body)
    return Pair(
        context.getString(titleResId, header),
        bodyResId?.let { context.getString(it, body) } ?: body,
    )
}
