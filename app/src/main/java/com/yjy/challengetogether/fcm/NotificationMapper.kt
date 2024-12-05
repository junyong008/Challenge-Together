package com.yjy.challengetogether.fcm

import android.content.Context
import com.yjy.common.designsystem.extensions.formatMessageWithContext
import com.yjy.model.common.notification.Notification
import com.yjy.model.common.notification.NotificationCategory
import com.yjy.model.common.notification.NotificationDestination
import com.yjy.platform.notifications.constants.NotificationChannels
import com.yjy.platform.notifications.model.PlatformNotification
import com.yjy.platform.notifications.model.PlatformNotificationNavigation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationMapper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun Notification.toPlatformNotification(): PlatformNotification {
        val (title, message) = type.formatMessageWithContext(context, header, body)

        val channelId = when (type.category) {
            NotificationCategory.CHALLENGE -> NotificationChannels.CHALLENGE
            NotificationCategory.COMMUNITY -> NotificationChannels.COMMUNITY
            NotificationCategory.COMMON -> NotificationChannels.CHALLENGE
        }

        val group = when (type.category) {
            NotificationCategory.CHALLENGE -> NotificationChannels.Groups.CHALLENGE
            NotificationCategory.COMMUNITY -> NotificationChannels.Groups.COMMUNITY
            NotificationCategory.COMMON -> NotificationChannels.Groups.CHALLENGE
        }

        val navigation = when (val dest = destination) {
            is NotificationDestination.StaredChallenge ->
                PlatformNotificationNavigation.StartedChallenge(dest.challengeId.toString())

            is NotificationDestination.WaitingChallenge ->
                PlatformNotificationNavigation.WaitingChallenge(dest.challengeId.toString())

            NotificationDestination.None -> null
        }

        return PlatformNotification(
            id = id,
            title = title,
            message = message,
            channelId = channelId,
            group = group,
            navigation = navigation,
        )
    }
}
