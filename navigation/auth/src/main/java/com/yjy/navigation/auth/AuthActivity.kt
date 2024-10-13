package com.yjy.navigation.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.navigation.Destination
import com.yjy.common.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

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
                AuthScreen(
                    navigateToService = {
                        val intent = navigator.createIntent(Destination.Service)
                        startActivity(intent)
                        finish()
                    },
                    onShowToast = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }
}
