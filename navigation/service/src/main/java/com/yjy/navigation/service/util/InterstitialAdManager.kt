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
import com.yjy.common.core.coroutines.MainScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
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
    @MainScope private val scope: CoroutineScope,
) {
    private val adMap = mutableMapOf<AdType, InterstitialAd?>()

    init {
        AdType.entries.forEach { loadAd(it) }
    }

    private fun loadAd(adType: AdType) {
        scope.launch {
            try {
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
            } catch (e: Exception) {
                Timber.e(e, "Failed to load ad")
                adMap[adType] = null
            }
        }
    }

    fun show(activity: Activity, adType: AdType) {
        val ad = adMap[adType]
        if (ad == null) {
            loadAd(adType)
            return
        }

        try {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    scope.launch {
                        adMap[adType] = null
                        loadAd(adType)
                    }
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    scope.launch {
                        adMap[adType] = null
                        loadAd(adType)
                    }
                }
            }
            ad.show(activity)
        } catch (e: Exception) {
            Timber.e(e, "Failed to show ad")
            loadAd(adType)
        }
    }

    fun cleanupAds() {
        scope.launch {
            adMap.values.forEach { ad ->
                try {
                    ad?.fullScreenContentCallback = null
                } catch (e: Exception) {
                    Timber.e(e, "Failed to cleanup ads")
                }
            }
        }
    }
}
