package com.yjy.feature.editchallenge.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.editchallenge.R

@Composable
internal fun ConfirmButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = CustomColorProvider.colorScheme.brand,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Text(
                text = stringResource(id = R.string.feature_editchallenge_edit),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
