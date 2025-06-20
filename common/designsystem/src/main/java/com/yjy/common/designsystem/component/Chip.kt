package com.yjy.common.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ChallengeTogetherChip(
    @StringRes textResId: Int,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chipText = stringResource(id = textResId)
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isSelected) {
                    CustomColorProvider.colorScheme.chipSelected
                } else {
                    CustomColorProvider.colorScheme.chipBackground
                },
            )
            .clickable { onSelectionChanged(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = chipText,
            color = if (isSelected) {
                CustomColorProvider.colorScheme.onChipSelected
            } else {
                CustomColorProvider.colorScheme.onChipBackground
            },
            style = if (isSelected) {
                MaterialTheme.typography.bodySmall
            } else {
                MaterialTheme.typography.labelSmall
            },
        )
    }
}

@ComponentPreviews
@Composable
fun ChallengeTogetherChipSelectedPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherChip(
            textResId = android.R.string.untitled,
            isSelected = true,
            onSelectionChanged = {},
        )
    }
}

@ComponentPreviews
@Composable
fun ChallengeTogetherChipPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherChip(
            textResId = android.R.string.untitled,
            isSelected = false,
            onSelectionChanged = {},
        )
    }
}
