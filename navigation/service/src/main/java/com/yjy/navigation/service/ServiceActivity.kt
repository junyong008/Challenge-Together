package com.yjy.navigation.service

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerLib
import com.appsflyer.deeplink.DeepLinkResult
import com.yjy.common.core.constants.DeepLinkConfig
import com.yjy.common.core.constants.DeepLinkPath
import com.yjy.common.core.constants.DeepLinkType
import com.yjy.common.core.constants.DeepLinkType.ID_PARAM
import com.yjy.common.core.constants.DeepLinkType.TYPE_PARAM
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.navigation.Destination
import com.yjy.common.navigation.Navigator
import com.yjy.navigation.service.util.InterstitialAdManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServiceActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var adManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDeepLinkHandling()

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDarkTheme },
                )
                onDispose {}
            }

            ChallengeTogetherTheme(isDarkTheme = isDarkTheme) {
                ServiceScreen(
                    navigateToAuth = {
                        val intent = navigator.createIntent(Destination.Auth)
                        startActivity(intent)
                        finish()
                    },
                    onShowToast = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    },
                    onShowAd = { adType ->
                        adManager.show(this, adType)
                    },
                    onFinishApp = { finish() },
                )
            }
        }
    }

    override fun onPause() {
        adManager.cleanupAds()
        super.onPause()
    }

    override fun onDestroy() {
        adManager.cleanupAds()
        super.onDestroy()
    }

    private fun setupDeepLinkHandling() {
        AppsFlyerLib.getInstance().subscribeForDeepLink { deepLinkResult ->
            if (deepLinkResult.status == DeepLinkResult.Status.FOUND) {
                val deepLink = deepLinkResult.deepLink

                val type = deepLink.getStringValue(TYPE_PARAM)
                val id = deepLink.getStringValue(ID_PARAM)?.toIntOrNull()

                if (type != null && id != null) {
                    val path = when (type) {
                        DeepLinkType.WAITING -> DeepLinkPath.WAITING
                        DeepLinkType.POST -> DeepLinkPath.POST
                        else -> return@subscribeForDeepLink
                    }

                    processDeepLink("/$path/$id")
                }
            }
        }
    }

    private fun processDeepLink(path: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = "${DeepLinkConfig.SCHEME_AND_HOST}$path".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
