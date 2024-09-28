package com.yjy.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun StableImage(
    @DrawableRes drawableResId: Int,
    description : String,
    modifier: Modifier = Modifier,
) {
    val painter = painterResource(id = drawableResId)
    Image(
        painter = painter,
        contentDescription = description,
        modifier = modifier,
    )
}

@Composable
fun LottieImage(
    modifier: Modifier = Modifier,
    animationResId: Int,
    repeatCount: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResId))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = repeatCount
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}