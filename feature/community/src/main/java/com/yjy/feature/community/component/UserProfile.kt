package com.yjy.feature.community.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.component.CircleMedal
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.common.Tier

@Composable
internal fun UserProfile(
    tier: Tier,
    modifier: Modifier = Modifier,
    innerPadding: Dp = 6.dp,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
) {
    Box(modifier = modifier.width(34.dp)) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(backgroundColor)
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            CircleMedal(tier = tier)
        }
    }
}
