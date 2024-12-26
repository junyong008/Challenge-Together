package com.yjy.challengetogether

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import com.yjy.challengetogether.BuildConfig.KAKAO_NATIVE_APP_KEY
import com.yjy.challengetogether.BuildConfig.NAVER_CLIENT_ID
import com.yjy.challengetogether.BuildConfig.NAVER_CLIENT_SECRET
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

        KakaoSdk.init(this, KAKAO_NATIVE_APP_KEY)
        NaverIdLoginSDK.initialize(this, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, getString(R.string.app_name))
    }
}
