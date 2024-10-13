package com.yjy.challengetogether.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yjy.challengetogether.R
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme

private const val SPLASH_DURATION = 300

@Composable
internal fun SplashScreen(
    navigateToAuth: () -> Unit,
    navigateToService: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(SPLASH_DURATION),
        )

        if (viewModel.getIsLoggedIn()) {
            navigateToService()
        } else {
            navigateToAuth()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.image_splash),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
                .alpha(alpha.value),
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SplashScreen(
                navigateToAuth = {},
                navigateToService = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
