package com.yjy.feature.community.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.community.R

@Composable
internal fun Guidelines() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.feature_community_rules_point_1_title),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_1_description),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_2_title),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_2_description),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_3_title),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_3_description),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}
