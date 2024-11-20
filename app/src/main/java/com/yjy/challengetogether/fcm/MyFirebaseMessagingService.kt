package com.yjy.challengetogether.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yjy.platform.notifications.NotificationHelper.postChallengeGiveUpNotification
import com.yjy.platform.notifications.NotificationHelper.postChallengeNewPostNotification
import com.yjy.platform.notifications.NotificationHelper.postChallengeResetNotification
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isEmpty()) return

        val header = remoteMessage.data[HEADER] ?: return
        val body = remoteMessage.data[BODY] ?: return
        val type = remoteMessage.data[TYPE] ?: return
        val linkIdx = remoteMessage.data[LINK_IDX]

        when (type) {
            FcmMessageType.CHALLENGE_RESET ->
                postChallengeResetNotification(this, header, body, linkIdx ?: return)

            FcmMessageType.CHALLENGE_DELETE ->
                postChallengeGiveUpNotification(this, header, body, linkIdx ?: return)

            FcmMessageType.CHALLENGE_POST ->
                postChallengeNewPostNotification(this, header, body, linkIdx ?: return)
        }

        Timber.d("Received FCM Message - Header: $header")
        Timber.d("Received FCM Message - Body: $body")
        Timber.d("Received FCM Message - Type: $type")
        Timber.d("Received FCM Message - LinkIdx: $linkIdx")
    }

    private companion object {
        const val HEADER = "header"
        const val BODY = "body"
        const val TYPE = "type"
        const val LINK_IDX = "linkIdx"
    }
}
