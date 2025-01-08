package com.yjy.navigation.service.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.navigation.service.R

@Composable
internal fun NetworkTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .height(IntrinsicSize.Min)
            .background(CustomColorProvider.colorScheme.red)
            .padding(16.dp)
            .animateContentSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.navigation_service_check_network_connection),
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@ComponentPreviews
@Composable
fun NetworkTopBarPreview() {
    ChallengeTogetherTheme {
        NetworkTopBar()
    }
}
