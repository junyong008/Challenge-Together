package com.yjy.challengetogether.initializer

import android.content.Context
import androidx.startup.Initializer
import com.appsflyer.AppsFlyerLib
import com.yjy.challengetogether.BuildConfig.APPS_FLYER_DEV_KEY

class AppsFlyerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AppsFlyerLib.getInstance().apply {
            init(APPS_FLYER_DEV_KEY, null, context)
            start(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
