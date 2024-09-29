package com.yjy.challengetogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yjy.challengetogether.ui.ChallengeTogetherApp
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        LIGHT_STATUS_BAR_COLOR,
                        DARK_STATUS_BAR_COLOR,
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        LIGHT_NAVIGATION_BAR_COLOR,
                        DARK_NAVIGATION_BAR_COLOR,
                    ) { isDarkTheme },
                )
                onDispose {}
            }

            ChallengeTogetherTheme(
                isDarkTheme = isDarkTheme,
            ) {
                ChallengeTogetherApp()
            }
        }
    }
}

private const val LIGHT_STATUS_BAR_COLOR = android.graphics.Color.TRANSPARENT
private const val DARK_STATUS_BAR_COLOR = android.graphics.Color.TRANSPARENT
private val LIGHT_NAVIGATION_BAR_COLOR = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val DARK_NAVIGATION_BAR_COLOR = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
