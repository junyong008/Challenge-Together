package com.yjy.navigation.service.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yjy.common.core.BuildConfig.ADMOB_INTERSTITIAL_ID_CHALLENGE_CREATE
import com.yjy.common.core.BuildConfig.ADMOB_INTERSTITIAL_ID_CHALLENGE_DELETE
import com.yjy.common.core.BuildConfig.ADMOB_INTERSTITIAL_ID_CHALLENGE_RESET
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class AdType(val adUnitId: String) {
    CHALLENGE_CREATE(ADMOB_INTERSTITIAL_ID_CHALLENGE_CREATE),
    CHALLENGE_DELETE(ADMOB_INTERSTITIAL_ID_CHALLENGE_DELETE),
    CHALLENGE_RESET(ADMOB_INTERSTITIAL_ID_CHALLENGE_RESET),
}

@Singleton
class InterstitialAdManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    private val adMap = mutableMapOf<AdType, InterstitialAd?>()

    init {
        AdType.entries.forEach { loadAd(it) }
    }

    private fun loadAd(adType: AdType) {
        InterstitialAd.load(
            appContext,
            adType.adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    adMap[adType] = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    adMap[adType] = null
                }
            },
        )
    }

    fun show(activity: Activity, adType: AdType) {
        val ad = adMap[adType]
        if (ad == null) {
            loadAd(adType)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                adMap[adType] = null
                loadAd(adType)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                adMap[adType] = null
                loadAd(adType)
            }
        }
        ad.show(activity)
    }
}
