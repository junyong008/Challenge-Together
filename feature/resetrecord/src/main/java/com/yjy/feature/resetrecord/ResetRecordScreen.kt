package com.yjy.feature.resetrecord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.preview.ResetRecordPreviewParameterProvider
import com.yjy.feature.resetrecord.component.ResetRecordChart
import com.yjy.feature.resetrecord.model.ResetRecordsUiState
import com.yjy.model.challenge.ResetRecord

@Composable
internal fun ResetRecordRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResetRecordViewModel = hiltViewModel(),
) {
    val resetRecords by viewModel.resetRecords.collectAsStateWithLifecycle()

    ResetRecordScreen(
        modifier = modifier,
        resetRecordsUiState = resetRecords,
        retryOnError = viewModel::retryOnError,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun ResetRecordScreen(
    modifier: Modifier = Modifier,
    resetRecordsUiState: ResetRecordsUiState = ResetRecordsUiState.Loading,
    retryOnError: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_resetrecord_title,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->

        when (resetRecordsUiState) {
            ResetRecordsUiState.Error -> ErrorBody(onClickRetry = retryOnError)
            ResetRecordsUiState.Loading -> {
                LoadingWheel(
                    modifier = Modifier
                        .padding(padding)
                        .background(CustomColorProvider.colorScheme.background),
                )
            }

            is ResetRecordsUiState.Success -> {
                if (resetRecordsUiState.resetRecords.isEmpty()) {
                    EmptyBody(
                        title = stringResource(id = R.string.feature_resetrecord_no_reset_records),
                        description = stringResource(id = R.string.feature_resetrecord_encouragement),
                    )
                } else {
                    ResetRecordBody(
                        resetRecords = resetRecordsUiState.resetRecords,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ResetRecordBody(
    resetRecords: List<ResetRecord>,
    modifier: Modifier = Modifier,
) {
    val chartData = resetRecords.sortedBy { it.resetDateTime }
    val listData = resetRecords.sortedByDescending { it.resetDateTime }

    Column(modifier = modifier.fillMaxSize()) {
        ResetRecordChart(
            data = chartData.map { it.resetDateTime },
            firstRecordInSeconds = chartData.first().recordInSeconds,
            valueSuffix = stringResource(id = R.string.feature_resetrecord_day_suffix),
            modifier = Modifier
                .fillMaxWidth()
                .background(CustomColorProvider.colorScheme.surface)
                .padding(start = 12.dp, end = 18.dp, top = 48.dp, bottom = 20.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            items(
                items = listData,
                key = { it.resetDateTime },
            ) { record ->
                RecordItem(record = record)
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RecordItem(record: ResetRecord) {
    Column {
        Text(
            text = formatLocalDateTime(record.resetDateTime),
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_resetrecord_record),
                    style = MaterialTheme.typography.labelSmall,
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                )
                Text(
                    text = formatTimeDuration(record.recordInSeconds),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CustomColorProvider.colorScheme.onSurface,
                )
            }
            if (record.content.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    color = CustomColorProvider.colorScheme.divider,
                    thickness = 1.dp,
                )
                Text(
                    text = stringResource(id = R.string.feature_resetrecord_content),
                    style = MaterialTheme.typography.labelSmall,
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = record.content,
                    style = MaterialTheme.typography.labelMedium,
                    color = CustomColorProvider.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.End),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun ResetRecordScreenPreview(
    @PreviewParameter(ResetRecordPreviewParameterProvider::class)
    resetRecords: List<ResetRecord>,
) {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ResetRecordScreen(
                resetRecordsUiState = ResetRecordsUiState.Success(resetRecords),
            )
        }
    }
}
