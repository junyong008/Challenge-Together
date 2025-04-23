package com.yjy.feature.resetrecord

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.Calendar
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SelectionMode
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.preview.ResetRecordPreviewParameterProvider
import com.yjy.feature.resetrecord.component.ResetRecordChart
import com.yjy.feature.resetrecord.model.ResetRecordsUiState
import com.yjy.feature.resetrecord.model.getOrNull
import com.yjy.model.challenge.ResetRecord
import kotlinx.coroutines.launch

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
    var isCalendarMode by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_resetrecord_title,
                rightContent = {
                    if (resetRecordsUiState.getOrNull()?.isNotEmpty() == true) {
                        ModeChangeButton(
                            onClick = { isCalendarMode = !isCalendarMode },
                            modifier = Modifier.padding(end = 4.dp),
                            isCalendarMode = isCalendarMode,
                        )
                    }
                },
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
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
                        isCalendarMode = isCalendarMode,
                        resetRecords = resetRecordsUiState.resetRecords,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeChangeButton(
    onClick: () -> Unit,
    isCalendarMode: Boolean,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.Chart),
            contentDescription = null,
            tint = if (isCalendarMode) {
                CustomColorProvider.colorScheme.onBackground
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ResetRecordBody(
    isCalendarMode: Boolean,
    resetRecords: List<ResetRecord>,
    modifier: Modifier = Modifier,
) {
    val chartData = resetRecords.sortedBy { it.id }
    val listData = resetRecords.sortedByDescending { it.id }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val scrollState by remember {
        derivedStateOf {
            Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var shouldSnap by rememberSaveable { mutableStateOf(false) }
    var listHeight by remember { mutableIntStateOf(0) }
    var itemHeight by remember { mutableIntStateOf(0) }
    var isDraggingChart by remember { mutableStateOf(false) }

    val currentVisibleIndex by remember {
        derivedStateOf {
            val firstVisibleItemOffset = listState.firstVisibleItemScrollOffset
            val firstVisibleIndex = listState.firstVisibleItemIndex

            if (itemHeight > 0 && firstVisibleItemOffset > itemHeight / 2) {
                firstVisibleIndex + 1
            } else {
                firstVisibleIndex
            }
        }
    }

    LaunchedEffect(currentVisibleIndex) {
        if (!isDraggingChart) {
            selectedIndex = currentVisibleIndex.coerceIn(0, listData.lastIndex)
        }
    }

    LaunchedEffect(scrollState) {
        if (listState.isScrollInProgress) {
            shouldSnap = true
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && !isDraggingChart && shouldSnap) {
            shouldSnap = false
            coroutineScope.launch {
                listState.animateScrollToItem(index = selectedIndex)
            }
        }
    }

    val itemHeightModifier = Modifier.onGloballyPositioned { coordinates ->
        itemHeight = coordinates.size.height
    }

    val listHeightModifier = Modifier.onGloballyPositioned {
        listHeight = it.size.height
    }

    val bottomPadding = remember(listHeight, itemHeight) {
        (listHeight - itemHeight).coerceAtLeast(0)
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (isCalendarMode) {
            Calendar(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                selectionMode = SelectionMode.SingleDate(listData[selectedIndex].resetDateTime.toLocalDate()),
                onDateSelected = { selectedDate ->
                    val matchedIndex = listData.indexOfFirst { it.resetDateTime.toLocalDate() == selectedDate }

                    if (matchedIndex != -1) {
                        selectedIndex = matchedIndex
                        coroutineScope.launch {
                            listState.scrollToItem(matchedIndex)
                        }
                    }
                },
                highlightedDateTimes = listData.map { it.resetDateTime },
            )
        } else {
            ResetRecordChart(
                data = chartData.map { it.resetDateTime },
                firstRecordInSeconds = chartData.first().recordInSeconds,
                valueSuffix = stringResource(id = R.string.feature_resetrecord_day_suffix),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomColorProvider.colorScheme.surface)
                    .padding(start = 12.dp, end = 18.dp, top = 48.dp, bottom = 20.dp),
                selectedIndex = (resetRecords.size - 1 - selectedIndex).coerceIn(0, chartData.lastIndex),
                onDateSelected = { newChartIndex ->
                    val newListIndex = listData.size - 1 - newChartIndex
                    selectedIndex = newListIndex
                    coroutineScope.launch {
                        listState.scrollToItem(newListIndex)
                    }
                },
                onDragStart = { isDraggingChart = true },
                onDragEnd = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index = selectedIndex)
                    }.invokeOnCompletion {
                        isDraggingChart = false
                    }
                },
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null,
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .then(listHeightModifier),
                contentPadding = PaddingValues(bottom = with(LocalDensity.current) { bottomPadding.toDp() }),
            ) {
                items(
                    items = listData,
                    key = { it.id },
                ) { record ->
                    RecordItem(
                        record = record,
                        isSelected = listData.indexOf(record) == selectedIndex,
                        modifier = itemHeightModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordItem(
    record: ResetRecord,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 1.dp,
                            color = CustomColorProvider.colorScheme.brand,
                            shape = MaterialTheme.shapes.medium,
                        )
                    } else {
                        Modifier
                    },
                )
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
                    modifier = Modifier.padding(end = 8.dp),
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
