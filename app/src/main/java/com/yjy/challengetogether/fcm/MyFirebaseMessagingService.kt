package com.yjy.challengetogether.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yjy.data.user.api.UserRepository
import com.yjy.model.common.notification.Notification
import com.yjy.model.common.notification.NotificationType
import com.yjy.platform.notifications.NotificationHelper
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
        if (notification.type == NotificationType.CHALLENGE_STARTED_POST) {
            if (userRepository.getMutedChallengeBoards().contains(notification.linkId)) return
        }

        NotificationHelper.postNotification(
            context = this,
            notification = with(notificationMapper) {
                notification.toPlatformNotification()
            },
        )
    }

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
