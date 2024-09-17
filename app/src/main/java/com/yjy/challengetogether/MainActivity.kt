package com.yjy.challengetogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
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

            ChallengeTogetherTheme(
                darkTheme = isDarkTheme,
            ) {
                ChallengeTogetherApp()
            }
        }
    }
}