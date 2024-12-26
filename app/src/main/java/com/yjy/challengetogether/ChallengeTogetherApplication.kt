package com.yjy.challengetogether

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.yjy.common.core.login.KakaoLoginManager
import com.yjy.common.core.login.NaverLoginManager
import dagger.hilt.android.HiltAndroidApp
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
    }
}
