package com.yjy.common.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

private const val DEFAULT_RANGE_START = 0
private const val DEFAULT_RANGE_END = 100
private const val DEFAULT_VISIBLE_ITEMS = 5

private const val MIN_SCALE = 0.8f
private const val SCALE_RANGE = 0.2f // 1.0f - MIN_SCALE
private const val MIN_ALPHA = 0.3f
private const val ALPHA_RANGE = 0.7f // 1.0f - MIN_ALPHA
private const val MAX_SCALE = 1.0f

@Composable
fun NumberPicker(
    selectedNumber: Int,
    onNumberChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = DEFAULT_RANGE_START..DEFAULT_RANGE_END,
    visibleItems: Int = DEFAULT_VISIBLE_ITEMS,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    contentColor: Color = CustomColorProvider.colorScheme.onSurface,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
) {
    val itemHeight = 40.dp
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val initialIndex = (selectedNumber - range.first).coerceIn(0, range.last - range.first)
    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // LazyColumn의 실제 높이 계산
    val lazyColumnHeight = itemHeight * visibleItems

    // 상하단 그라데이션 설정
    val gradientBrush = remember(lazyColumnHeight, backgroundColor) {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.8f),
                backgroundColor.copy(alpha = 0.0f),
                backgroundColor.copy(alpha = 0.0f),
                backgroundColor.copy(alpha = 0.8f),
            ),
            startY = 0f,
            endY = with(density) { lazyColumnHeight.toPx() },
        )
    }

    // 이전에 선택된 인덱스를 저장하기 위한 변수. 인덱스가 변경됐을때만 onNumberChange 호출
    var lastSelectedIndex by remember { mutableIntStateOf(-1) }

    // Divider 그리기 위한 너비 계산
    var parentWidth by remember { mutableIntStateOf(0) }

    // 스크롤이 멈췄을 때 가장 가까운 아이템으로 스크롤
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { !it }
            .collectLatest {
                val layoutInfo = state.layoutInfo
                if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                    val closestItem = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                        val itemCenter = item.offset + item.size / 2f
                        abs(itemCenter - viewportCenter)
                    }
                    closestItem?.let { item ->
                        val centerOffset = item.offset + item.size / 2f - viewportCenter

                        // centerOffset이 1픽셀 이상일 때만 스크롤 애니메이션 실행
                        if (abs(centerOffset) > 1f) {
                            state.animateScrollBy(centerOffset)
                        }

                        // 선택된 인덱스가 변경되었을 때만 onNumberChange 호출
                        if (item.index != lastSelectedIndex) {
                            lastSelectedIndex = item.index
                            onNumberChange(range.elementAt(item.index))
                        }
                    }
                }
            }
    }

    // 각 아이템의 scale과 alpha를 계산하는 derivedStateOf
    val scaleAndAlphaMap by remember {
        derivedStateOf {
            val map = mutableMapOf<Int, Pair<Float, Float>>()
            val layoutInfo = state.layoutInfo
            if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f

                // maxDistance를 조정하여 가운데 아이템에만 최대값이 적용되도록 함
                val maxDistance = itemHeightPx * (visibleItems / 2)
                for (item in layoutInfo.visibleItemsInfo) {
                    val itemCenter = item.offset + item.size / 2f
                    val distanceFromCenter = abs(itemCenter - viewportCenter)

                    val fraction = (MAX_SCALE - (distanceFromCenter / maxDistance)).coerceIn(0f, MAX_SCALE)
                    val scale = MIN_SCALE + SCALE_RANGE * fraction
                    val alpha = MIN_ALPHA + ALPHA_RANGE * fraction
                    map[item.index] = Pair(scale, alpha)
                }
            }
            map
        }
    }

    Box(
        modifier = modifier
            .widthIn(min = 45.dp)
            .onGloballyPositioned { coordinates ->
                parentWidth = with(density) { coordinates.size.width.toDp().value.toInt() }
            },
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .height(lazyColumnHeight),
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItems / 2)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(range.toList()) { index, number ->
                val (scale, alpha) = scaleAndAlphaMap[index] ?: Pair(MIN_SCALE, MIN_ALPHA)
                Text(
                    text = number.toString(),
                    style = textStyle.copy(
                        fontSize = textStyle.fontSize * scale,
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .alpha(alpha),
                    color = contentColor,
                )
            }
        }

        // 상하단 그라데이션 오버레이
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(gradientBrush),
        )

        HorizontalDivider(
            modifier = Modifier
                .width(parentWidth.dp)
                .align(Alignment.Center)
                .offset(y = (-itemHeight / 2)),
            color = contentColor,
            thickness = 1.dp,
        )
        HorizontalDivider(
            modifier = Modifier
                .width(parentWidth.dp)
                .align(Alignment.Center)
                .offset(y = (itemHeight / 2)),
            color = contentColor,
            thickness = 1.dp,
        )
    }
}

@ComponentPreviews
@Composable
fun NumberPickerPreview() {
    ChallengeTogetherTheme {
        Box(
            modifier = Modifier.background(CustomColorProvider.colorScheme.surface),
        ) {
            NumberPicker(
                selectedNumber = 10,
                onNumberChange = {},
                visibleItems = 11,
            )
        }
    }
}
