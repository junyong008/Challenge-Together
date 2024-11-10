package com.yjy.challengetogether.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yjy.challengetogether.BuildConfig

class FirebaseCrashlyticsInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}