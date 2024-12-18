package com.yjy.challengetogether.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yjy.challengetogether.di.FcmEntryPoint
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.common.notification.Notification
import com.yjy.model.common.notification.NotificationSettingFlags
import com.yjy.model.common.notification.NotificationType
import com.yjy.platform.notifications.NotificationHelper
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val notificationMapper: NotificationMapper by lazy {
        EntryPoints.get(applicationContext, FcmEntryPoint::class.java).notificationMapper()
    }

    private val userRepository: UserRepository by lazy {
        EntryPoints.get(applicationContext, FcmEntryPoint::class.java).userRepository()
    }

    private val appLockRepository: AppLockRepository by lazy {
        EntryPoints.get(applicationContext, FcmEntryPoint::class.java).appLockRepository()
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isEmpty()) return

        val notification = extractNotification(remoteMessage.data) ?: return

        serviceScope.launch {
            handleFcmMessage(notification)
        }
        logNotification(notification)
    }

    private fun extractNotification(data: Map<String, String>): Notification? {
        val header = data[HEADER] ?: return null
        val body = data[BODY] ?: return null
        val type = data[TYPE] ?: return null
        val linkIdx = data[LINK_IDX]?.toIntOrNull() ?: return null

        return Notification(
            id = linkIdx,
            createdDateTime = LocalDateTime.now(),
            header = header,
            body = body,
            type = NotificationType.fromString(type),
            linkId = linkIdx,
        )
    }

    private suspend fun handleFcmMessage(notification: Notification) {
        if (isNotificationMuted(notification.type, notification.linkId)) return
        if (isNotificationSettingDisabled(notification.type)) return

        NotificationHelper.postNotification(
            context = this,
            notification = with(notificationMapper) {
                notification.toPlatformNotification(
                    shouldHideContent = appLockRepository.shouldHideNotificationContents.first(),
                )
            },
        )
    }

    private suspend fun isNotificationSettingDisabled(notificationType: NotificationType): Boolean {
        val settings = userRepository.getNotificationSettings()

        if (!NotificationSettingFlags.isEnabled(settings, NotificationSettingFlags.ALL)) {
            return true
        }

        val settingFlag = when (notificationType) {
            NotificationType.CHALLENGE_COMPLETE,
            -> NotificationSettingFlags.GOAL_ACHIEVED

            NotificationType.CHALLENGE_JOIN,
            NotificationType.CHALLENGE_LEAVE,
            -> NotificationSettingFlags.WAITING_ROOM_PARTICIPANTS

            NotificationType.CHALLENGE_START,
            NotificationType.CHALLENGE_DELETE,
            -> NotificationSettingFlags.WAITING_ROOM_UPDATE

            NotificationType.CHALLENGE_RESET,
            NotificationType.CHALLENGE_GIVE_UP,
            NotificationType.CHALLENGE_FORCE_REMOVE,
            -> NotificationSettingFlags.CHALLENGE_ROOM_PROGRESS

            NotificationType.CHALLENGE_STARTED_POST,
            NotificationType.CHALLENGE_WAITING_POST,
            -> NotificationSettingFlags.CHALLENGE_BOARD

            NotificationType.COMMUNITY_POST -> NotificationSettingFlags.COMMUNITY_COMMENT
            NotificationType.COMMUNITY_COMMENT -> NotificationSettingFlags.COMMUNITY_REPLY
            NotificationType.COMMON -> return false
        }

        return !NotificationSettingFlags.isEnabled(settings, settingFlag)
    }

    private suspend fun isNotificationMuted(
        notificationType: NotificationType,
        notificationLinkId: Int,
    ) = when (notificationType) {
        NotificationType.CHALLENGE_WAITING_POST,
        NotificationType.CHALLENGE_STARTED_POST,
        -> isChallengeBoardMuted(notificationLinkId)

        NotificationType.COMMUNITY_POST,
        NotificationType.COMMUNITY_COMMENT,
        -> isCommunityPostMuted(notificationLinkId)

        else -> false
    }

    private suspend fun isChallengeBoardMuted(challengeBoardId: Int): Boolean =
        userRepository.getMutedChallengeBoards().contains(challengeBoardId)

    private suspend fun isCommunityPostMuted(communityPostId: Int): Boolean =
        userRepository.getMutedCommunityPosts().contains(communityPostId)

    private fun logNotification(notification: Notification) {
        with(notification) {
            Timber.d("Received FCM Notification - ID: $id")
            Timber.d("Received FCM Notification - Header: $header")
            Timber.d("Received FCM Notification - Body: $body")
            Timber.d("Received FCM Notification - Type: ${type.type}")
            Timber.d("Received FCM Notification - LinkIndex: $linkId")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private companion object {
        const val HEADER = "header"
        const val BODY = "body"
        const val TYPE = "type"
        const val LINK_IDX = "linkIdx"
    }
}
