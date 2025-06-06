package com.yjy.challengetogether.fcm

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.yjy.data.user.api.FcmTokenProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class DefaultFcmTokenProvider @Inject constructor() : FcmTokenProvider {
    override suspend fun getToken(): String = try {
        Firebase.messaging.token.await()
    } catch (e: Exception) {
        Timber.e(e, "Failed to get FCM token")
        ""
    }
}
