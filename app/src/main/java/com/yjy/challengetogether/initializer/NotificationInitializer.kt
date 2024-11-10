package com.yjy.challengetogether.initializer

import android.content.Context
import androidx.startup.Initializer
import com.yjy.platform.notifications.NotificationHelper

class NotificationInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        NotificationHelper.createNotificationChannels(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
