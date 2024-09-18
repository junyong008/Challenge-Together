package com.yjy.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

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