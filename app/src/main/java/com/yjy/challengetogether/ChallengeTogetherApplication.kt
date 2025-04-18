package com.yjy.challengetogether

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yjy.common.core.login.KakaoLoginManager
import com.yjy.common.core.login.NaverLoginManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ChallengeTogetherApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        KakaoLoginManager.init(this)
        NaverLoginManager.init(this, getString(R.string.app_name))

        val crashlytics = FirebaseCrashlytics.getInstance()
        val locale = resources.configuration.locales[0]
        crashlytics.setCustomKey("language", locale.language)
        crashlytics.setCustomKey("region", locale.country)

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@ChallengeTogetherApplication)
        }
    }
}
