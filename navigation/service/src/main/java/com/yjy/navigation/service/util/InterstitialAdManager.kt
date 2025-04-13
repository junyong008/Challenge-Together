package com.yjy.navigation.service.util

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yjy.common.core.BuildConfig.ADMOB_INTERSTITIAL_ID_CHALLENGE_CREATE
import com.yjy.common.core.BuildConfig.ADMOB_INTERSTITIAL_ID_CHALLENGE_DELETE
import com.yjy.common.core.BuildConfig.ADMOB_INTERSTITIAL_ID_CHALLENGE_RESET
import timber.log.Timber

enum class AdType(val adUnitId: String) {
    CHALLENGE_CREATE(ADMOB_INTERSTITIAL_ID_CHALLENGE_CREATE),
    CHALLENGE_DELETE(ADMOB_INTERSTITIAL_ID_CHALLENGE_DELETE),
    CHALLENGE_RESET(ADMOB_INTERSTITIAL_ID_CHALLENGE_RESET),
}

class InterstitialAdManager(private val activity: Activity) {

    private val adMap = mutableMapOf<AdType, InterstitialAd?>()

    fun loadAds() {
        AdType.entries.forEach { adType ->
            Timber.d("loadAd for $adType")
            loadAd(adType)
        }
    }

    private fun loadAd(adType: AdType) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            adType.adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    super.onAdLoaded(ad)
                    Timber.d("Interstitial ad loaded for $adType")
                    adMap[adType] = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Timber.e("Interstitial ad failed to load for $adType: ${error.message}")
                    adMap[adType] = null
                }
            },
        )
    }

    fun showAd(adType: AdType) {
        val ad = adMap[adType]
        Timber.d("Attempting to show ad for $adType")

        if (ad == null) {
            Timber.e("Interstitial ad is not ready for $adType")
            loadAd(adType)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Timber.d("Interstitial ad showed for $adType")
            }

            override fun onAdDismissedFullScreenContent() {
                Timber.d("Interstitial ad dismissed for $adType")
                adMap[adType] = null
                loadAd(adType)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Timber.e("Interstitial ad failed to show for $adType: ${error.message}")
                adMap[adType] = null
                loadAd(adType)
            }

            override fun onAdClicked() {
                Timber.d("Interstitial ad clicked for $adType")
            }

            override fun onAdImpression() {
                Timber.d("Interstitial ad impression logged for $adType")
            }
        }

        activity.runOnUiThread {
            try {
                Timber.d("Calling ad.show() for $adType")
                ad.show(activity)
            } catch (e: Exception) {
                Timber.e(e, "Exception while showing interstitial ad for $adType")
            }
        }
    }
}
