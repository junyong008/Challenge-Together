package com.yjy.platform.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import com.yjy.common.core.constants.DeepLinkConfig
import com.yjy.platform.notifications.constants.NotificationChannels
import com.yjy.platform.notifications.model.PlatformNotification
import com.yjy.platform.notifications.model.PlatformNotificationNavigation
import timber.log.Timber

object NotificationHelper {

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        createChallengeNotificationChannel(context)
        createCommunityNotificationChannel(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChallengeNotificationChannel(context: Context) {
        val challengeChannel = NotificationChannel(
            NotificationChannels.CHALLENGE,
            context.getString(R.string.platform_notifications_channel_challenge_name),
            NotificationManager.IMPORTANCE_HIGH,
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(challengeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createCommunityNotificationChannel(context: Context) {
        val communityChannel = NotificationChannel(
            NotificationChannels.COMMUNITY,
            context.getString(R.string.platform_notifications_channel_community_name),
            NotificationManager.IMPORTANCE_HIGH,
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(communityChannel)
    }

    private fun Context.isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            checkSelfPermission(this, permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED
        }
    }

    private fun Context.createNotification(
        channelId: String,
        block: NotificationCompat.Builder.() -> Unit,
    ): Notification {
        createNotificationChannels(this)
        return NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply(block)
            .build()
    }

    fun postNotification(context: Context, notification: PlatformNotification) = with(context) {
        if (!isNotificationPermissionGranted()) return

        val androidNotification = createNotification(notification.channelId) {
            setContentTitle(notification.title)
                .setContentText(notification.message)
                .setContentIntent(
                    notification.navigation?.let { navigation ->
                        createPendingIntent(navigation)
                    },
                )
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(notification.group)
                .setAutoCancel(true)
        }

        try {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notification.id, androidNotification)
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to post notification")
        }
    }

    private fun Context.createPendingIntent(
        navigation: PlatformNotificationNavigation,
    ): PendingIntent = PendingIntent.getActivity(
        this,
        navigation.requestCode,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = "${DeepLinkConfig.SCHEME_AND_HOST}/${navigation.deepLinkPath}/${navigation.param}".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
