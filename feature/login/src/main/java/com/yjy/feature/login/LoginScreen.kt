package com.yjy.feature.login

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    LoginScreen(
        modifier = modifier,
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Text(
            text = "TEXT텍스트",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}