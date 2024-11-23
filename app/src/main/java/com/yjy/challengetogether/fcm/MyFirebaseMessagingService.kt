package com.yjy.challengetogether.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yjy.data.user.api.UserRepository
import com.yjy.platform.notifications.NotificationHelper.postChallengeForceRemoveNotification
import com.yjy.platform.notifications.NotificationHelper.postChallengeGiveUpNotification
import com.yjy.platform.notifications.NotificationHelper.postChallengeResetNotification
import com.yjy.platform.notifications.NotificationHelper.postStartedChallengeNewPostNotification
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val userRepository: UserRepository by lazy {
        EntryPoints.get(applicationContext, FcmEntryPoint::class.java).userRepository()
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isEmpty()) return

        val messageData = extractMessageData(remoteMessage.data) ?: return

        serviceScope.launch {
            handleFcmMessage(messageData)
        }
        logMessageData(messageData)
    }

    private fun extractMessageData(data: Map<String, String>): MessageData? {
        val header = data[HEADER] ?: return null
        val body = data[BODY] ?: return null
        val type = data[TYPE] ?: return null
        val linkIdx = data[LINK_IDX] ?: return null

        return MessageData(
            header = header,
            body = body,
            type = type,
            linkIdx = linkIdx,
        )
    }

    private suspend fun handleFcmMessage(messageData: MessageData) {
        when (messageData.type) {
            FcmMessageType.CHALLENGE_RESET ->
                postChallengeResetNotification(
                    this,
                    messageData.header,
                    messageData.body,
                    messageData.linkIdx,
                )

            FcmMessageType.CHALLENGE_DELETE ->
                postChallengeGiveUpNotification(
                    this,
                    messageData.header,
                    messageData.body,
                    messageData.linkIdx,
                )

            FcmMessageType.CHALLENGE_STARTED_POST -> {
                val linkIdxInt = messageData.linkIdx.toIntOrNull() ?: return
                if (userRepository.getMutedChallengeBoards().contains(linkIdxInt)) return
                postStartedChallengeNewPostNotification(
                    this,
                    messageData.header,
                    messageData.body,
                    messageData.linkIdx,
                )
            }

            FcmMessageType.CHALLENGE_FORCE_REMOVE ->
                postChallengeForceRemoveNotification(
                    this,
                    messageData.header,
                    messageData.body,
                    messageData.linkIdx,
                )
        }
    }

    private fun logMessageData(messageData: MessageData) {
        with(messageData) {
            Timber.d("Received FCM Message - Header: $header")
            Timber.d("Received FCM Message - Body: $body")
            Timber.d("Received FCM Message - Type: $type")
            Timber.d("Received FCM Message - LinkIdx: $linkIdx")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private data class MessageData(
        val header: String,
        val body: String,
        val type: String,
        val linkIdx: String,
    )

    private companion object {
        const val HEADER = "header"
        const val BODY = "body"
        const val TYPE = "type"
        const val LINK_IDX = "linkIdx"
    }
}
