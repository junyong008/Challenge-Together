package com.yjy.platform.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import com.yjy.platform.notifications.NotificationConst.CHALLENGE_NOTIFICATION_GROUP
import com.yjy.platform.notifications.NotificationConst.CHANNEL_CHALLENGE_ID
import com.yjy.platform.notifications.NotificationConst.CHANNEL_COMMUNITY_ID
import com.yjy.platform.notifications.NotificationConst.DEEP_LINK_SCHEME_AND_HOST
import com.yjy.platform.notifications.NotificationConst.SERVICE_ACTIVITY_NAME
import com.yjy.platform.notifications.NotificationConst.STARTED_CHALLENGE_PATH
import com.yjy.platform.notifications.NotificationConst.STARTED_CHALLENGE_REQUEST_CODE

object NotificationHelper {

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        createChallengeNotificationChannel(context)
        createCommunityNotificationChannel(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChallengeNotificationChannel(context: Context) {
        val challengeChannel = NotificationChannel(
            CHANNEL_CHALLENGE_ID,
            context.getString(R.string.platform_notifications_channel_challenge_name),
            NotificationManager.IMPORTANCE_HIGH,
        )

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(challengeChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createCommunityNotificationChannel(context: Context) {
        val communityChannel = NotificationChannel(
            CHANNEL_COMMUNITY_ID,
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

    private fun Context.ensureNotificationChannelsExists() {
        createNotificationChannels(this)
    }

    private fun Context.createChallengeNotification(
        block: NotificationCompat.Builder.() -> Unit,
    ): Notification {
        ensureNotificationChannelsExists()
        return NotificationCompat.Builder(this, CHANNEL_CHALLENGE_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply(block)
            .build()
    }

    private fun Context.createCommunityNotification(
        block: NotificationCompat.Builder.() -> Unit,
    ): Notification {
        ensureNotificationChannelsExists()
        return NotificationCompat.Builder(this, CHANNEL_COMMUNITY_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply(block)
            .build()
    }

    fun postChallengeResetNotification(
        context: Context,
        header: String,
        body: String,
        linkIdx: String,
    ) = with(context) {
        if (!isNotificationPermissionGranted()) return

        val title = getString(R.string.platform_notifications_participant_reset, header)
        val message = getString(R.string.platform_notifications_user_reset, body)

        val notification = createChallengeNotification {
            setContentTitle(title)
                .setContentText(message)
                .setContentIntent(
                    startedChallengePendingIntent(linkIdx)
                )
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(CHALLENGE_NOTIFICATION_GROUP)
                .setAutoCancel(true)
                .build()
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(linkIdx.hashCode(), notification)
    }

    fun postChallengeGiveUpNotification(
        context: Context,
        header: String,
        body: String,
        linkIdx: String,
    ) = with(context) {
        if (!isNotificationPermissionGranted()) return

        val title = getString(R.string.platform_notifications_participant_give_up, header)
        val message = getString(R.string.platform_notifications_user_give_up, body)

        val notification = createChallengeNotification {
            setContentTitle(title)
                .setContentText(message)
                .setContentIntent(
                    startedChallengePendingIntent(linkIdx)
                )
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(CHALLENGE_NOTIFICATION_GROUP)
                .setAutoCancel(true)
                .build()
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(linkIdx.hashCode(), notification)
    }

    private fun Context.startedChallengePendingIntent(
        challengeId: String,
    ): PendingIntent? = PendingIntent.getActivity(
        this,
        STARTED_CHALLENGE_REQUEST_CODE,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = "$DEEP_LINK_SCHEME_AND_HOST/$STARTED_CHALLENGE_PATH/$challengeId".toUri()
            component = ComponentName(
                packageName,
                SERVICE_ACTIVITY_NAME,
            )
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
