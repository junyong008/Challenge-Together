package com.yjy.challengetogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import com.yjy.challengetogether.splash.SplashScreen
import com.yjy.common.core.extensions.startActivityWithAnimation
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.navigation.auth.AuthActivity
import com.yjy.navigation.service.ServiceActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                ChallengeTogetherBackground {
                    SplashScreen(
                        isDarkTheme = isDarkTheme,
                        navigateToAuth = { startActivityWithAnimation<AuthActivity>(withFinish = true) },
                        navigateToService = { startActivityWithAnimation<ServiceActivity>(withFinish = true) },
                    )
                }
            }
        }
    }
}
