package com.yjy.navigation.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yjy.navigation.auth.ui.AuthScreen
import com.yjy.navigation.auth.ui.rememberAuthNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navigator = rememberAuthNavController()

            AuthScreen(navigator = navigator)
        }
    }
}