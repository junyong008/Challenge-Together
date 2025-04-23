package com.yjy.feature.resetrecord.component

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.BOTTOM_TEXT_BASELINE_PADDING
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.CENTER_ALIGNMENT_DIVISOR
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.DATA_POINT_COUNT_THRESHOLD
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.DATE_BOX_CORNER_RADIUS
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.DEFAULT_CANVAS_HEIGHT
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.DEFAULT_CANVAS_PADDING
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.GRID_LINE_COUNT
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.GRID_LINE_STROKE_WIDTH
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.RIPPLE_EFFECT_DURATION
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.RIPPLE_EFFECT_INIT_ALPHA
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.RIPPLE_EFFECT_INIT_SCALE
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.RIPPLE_EFFECT_TARGET_ALPHA
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.RIPPLE_EFFECT_TARGET_SCALE
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.SELECTION_LINE_ALPHA
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.SELECTION_LINE_STROKE_WIDTH
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.TEXT_BASELINE_ADJUSTMENT
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.VALUE_BOX_CORNER_RADIUS
import com.yjy.feature.resetrecord.component.ResetRecordChartDefaults.VALUE_BOX_TOP_PADDING
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

private object ResetRecordChartDefaults {
    // Layout Constants
    const val DEFAULT_CANVAS_HEIGHT = 200
    const val DEFAULT_CANVAS_PADDING = 16
    const val BOTTOM_TEXT_BASELINE_PADDING = 5
    const val VALUE_BOX_TOP_PADDING = 8
    const val TEXT_BASELINE_ADJUSTMENT = 3

    // Grid Constants
    const val GRID_LINE_COUNT = 3
    const val GRID_LINE_STROKE_WIDTH = 1
    const val GRID_DASH_SIZE = 10f
    const val GRID_DASH_INTERVAL = 10f

    // Selection Constants
    const val SELECTION_LINE_ALPHA = 0.3f
    const val SELECTION_LINE_STROKE_WIDTH = 2
    const val SELECTION_DASH_SIZE = 15f
    const val SELECTION_DASH_INTERVAL = 15f

    // Chart Layout Constants
    // startFromLeft 가 꺼져 있고, DATA_POINT_COUNT_THRESHOLD 보다 데이터 개수가 적으면 차트를 중앙에 배치
    const val DATA_POINT_COUNT_THRESHOLD = 4
    const val CENTER_ALIGNMENT_DIVISOR = 4f

    // Corner Radius
    const val VALUE_BOX_CORNER_RADIUS = 50
    const val DATE_BOX_CORNER_RADIUS = 4

    // Ripple
    const val RIPPLE_EFFECT_DURATION = 800
    const val RIPPLE_EFFECT_INIT_SCALE = 1f
    const val RIPPLE_EFFECT_TARGET_SCALE = 2f
    const val RIPPLE_EFFECT_INIT_ALPHA = 0.8f
    const val RIPPLE_EFFECT_TARGET_ALPHA = 0f
}

@Composable
internal fun ResetRecordChart(
    data: List<LocalDateTime>,
    firstRecordInSeconds: Long,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    onDateSelected: (Int) -> Unit = {},
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {},
    valueSuffix: String = "",
    lineColor: Color = CustomColorProvider.colorScheme.brandDim,
    donutBackgroundColor: Color = CustomColorProvider.colorScheme.surface,
    valueBoxBackgroundColor: Color = CustomColorProvider.colorScheme.brand,
    valueTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    valueTextColor: Color = CustomColorProvider.colorScheme.onBrand,
    scaleTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    scaleTextColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
    dateTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    dateTextColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
    dateBoxBackgroundColor: Color = CustomColorProvider.colorScheme.background,
    dateBoxTextColor: Color = CustomColorProvider.colorScheme.onBackground,
    lineWidth: Dp = 4.dp,
    paddingPercent: Float = 0.2f,
    endPointRadius: Dp = 6.dp,
    valueBoxPadding: Dp = 8.dp,
    dateBoxPadding: Dp = 8.dp,
    scaleToChartSpacing: Dp = 16.dp,
    dateToChartSpacing: Dp = 16.dp,
    dateSpacing: Dp = 36.dp,
    startFromLeft: Boolean = true,
    enableAnimation: Boolean = true,
) {
    val context = LocalContext.current
    var points by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var lastDragPoint by remember { mutableStateOf<Int?>(null) }
    var isFirstRender by rememberSaveable { mutableStateOf(true) }
    var selectedIndexState by rememberSaveable(selectedIndex) {
        mutableIntStateOf(selectedIndex ?: (data.size - 1))
    }

    val selectedPoint = if (enableAnimation && !isFirstRender) {
        animateOffsetAsState(
            targetValue = points.getOrNull(selectedIndex ?: 0) ?: Offset.Zero,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            ),
            label = "LineChart Animation",
        ).value
    } else {
        points.getOrNull(selectedIndex ?: 0) ?: Offset.Zero
    }

    // 초기 렌더링 시 마지막 데이터 포인트 선택
    LaunchedEffect(points) {
        if (isFirstRender && points.isNotEmpty()) {
            selectedIndexState = data.size - 1
            onDateSelected(selectedIndexState)
            isFirstRender = false
        }
    }

    // 외부에서 selectedIndex가 변경될 때 처리
    LaunchedEffect(selectedIndex) {
        if (!isFirstRender && selectedIndex != null) {
            selectedIndexState = selectedIndex
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Ripple Transition")

    val rippleScale by infiniteTransition.animateFloat(
        initialValue = RIPPLE_EFFECT_INIT_SCALE,
        targetValue = RIPPLE_EFFECT_TARGET_SCALE,
        animationSpec = infiniteRepeatable(
            animation = tween(RIPPLE_EFFECT_DURATION),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Ripple Scale",
    )

    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = RIPPLE_EFFECT_INIT_ALPHA,
        targetValue = RIPPLE_EFFECT_TARGET_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(RIPPLE_EFFECT_DURATION),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Ripple Alpha",
    )

    // 날짜 포맷팅
    val currentYear = remember { LocalDateTime.now().year }
    val normalDateFormatter = DateTimeFormatter.ofPattern("M.d")
    val selectedDateFormatter = DateTimeFormatter.ofPattern("yy.M.d")
    val dates = data.map { it.format(normalDateFormatter) }

    // 첫날은 지정된 값 사용, 나머지는 날짜 간격으로 계산
    val daysData = buildList {
        add(firstRecordInSeconds.toFloat() / SECONDS_PER_DAY)

        data.windowed(2) { window ->
            val seconds = ChronoUnit.SECONDS.between(window[0], window[1]).toFloat()
            add(seconds / SECONDS_PER_DAY)
        }
    }

    val timeIntervals = data.windowed(2) { window ->
        ChronoUnit.SECONDS.between(window[0], window[1]).toFloat()
    }
    val totalInterval = timeIntervals.sum().coerceAtLeast(1f)

    val actualMaxValue = daysData.maxOrNull() ?: 0f
    val actualMinValue = daysData.minOrNull() ?: 0f
    val valueRange = (actualMaxValue - actualMinValue).let { range ->
        if (range <= 0f) 1f else range
    }

    val paddingValue = valueRange * paddingPercent
    val maxValue = actualMaxValue + paddingValue
    val minValue = (actualMinValue - paddingValue).coerceAtLeast(0f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(DEFAULT_CANVAS_HEIGHT.dp)
            .padding(DEFAULT_CANVAS_PADDING.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    // 터치 다운 이벤트
                    val down = awaitFirstDown()
                    onDragStart()

                    val initialPoint = points.indexOfMinBy { point ->
                        abs(point.x - down.position.x)
                    }
                    lastDragPoint = initialPoint
                    if (initialPoint != null) {
                        onDateSelected(initialPoint)
                    }

                    // 드래그 이벤트
                    do {
                        val event = awaitPointerEvent()
                        val position = event.changes.first().position

                        val nearestPoint = points.indexOfMinBy { point ->
                            abs(point.x - position.x)
                        }

                        // 가장 가까운 포인트가 변경되었을 때만 콜백 호출
                        if (nearestPoint != null && nearestPoint != lastDragPoint) {
                            lastDragPoint = nearestPoint
                            onDateSelected(nearestPoint)
                        }
                    } while (event.changes.any { it.pressed })

                    // 드래그 종료
                    onDragEnd()
                    lastDragPoint = null
                }
            },
    ) {
        // Text Paint 설정
        val valueTextPaint = Paint().apply {
            textSize = valueTextStyle.fontSize.toPx()
            textAlign = Paint.Align.CENTER
            val fontResId = when (valueTextStyle.fontWeight) {
                FontWeight.Bold -> R.font.pretendard_bold
                FontWeight.Medium -> R.font.pretendard_medium
                else -> R.font.pretendard_medium
            }
            typeface = ResourcesCompat.getFont(context, fontResId)
            color = valueTextColor.toArgb()
        }

        val scaleTextPaint = Paint().apply {
            textSize = scaleTextStyle.fontSize.toPx()
            textAlign = Paint.Align.RIGHT
            val fontResId = when (scaleTextStyle.fontWeight) {
                FontWeight.Bold -> R.font.pretendard_bold
                FontWeight.Medium -> R.font.pretendard_medium
                else -> R.font.pretendard_medium
            }
            typeface = ResourcesCompat.getFont(context, fontResId)
            color = scaleTextColor.toArgb()
        }

        val dateTextPaint = Paint().apply {
            textSize = dateTextStyle.fontSize.toPx()
            textAlign = Paint.Align.CENTER
            val fontResId = when (dateTextStyle.fontWeight) {
                FontWeight.Bold -> R.font.pretendard_bold
                FontWeight.Medium -> R.font.pretendard_medium
                else -> R.font.pretendard_medium
            }
            typeface = ResourcesCompat.getFont(context, fontResId)
            color = dateTextColor.toArgb()
        }

        // 스케일 텍스트들의 최대 너비 계산
        val adjustedValueRange = (maxValue - minValue).let { range ->
            if (range <= 0f) 1f else range
        }
        var maxScaleTextWidth = 0f

        for (i in 0..GRID_LINE_COUNT) {
            val value = (maxValue - (adjustedValueRange * i / GRID_LINE_COUNT)).coerceAtLeast(0f)
            val scaleText = "%.1f %s".format(value, valueSuffix)
            val textBounds = Rect()
            scaleTextPaint.getTextBounds(scaleText, 0, scaleText.length, textBounds)
            maxScaleTextWidth = maxOf(maxScaleTextWidth, textBounds.width().toFloat())
        }

        // 차트 영역 계산
        val dateToChartSpace = dateToChartSpacing.toPx()
        val chartStartX = maxScaleTextWidth + scaleToChartSpacing.toPx()
        val chartWidth = size.width - chartStartX
        val height = size.height - 20.dp.toPx() - dateToChartSpace

        // points 계산
        points = buildList {
            var currentX = if (startFromLeft) {
                chartStartX
            } else {
                if (data.size < DATA_POINT_COUNT_THRESHOLD) {
                    chartStartX + (chartWidth - (chartWidth * (data.size - 1) / CENTER_ALIGNMENT_DIVISOR)) / 2
                } else {
                    chartStartX
                }
            }

            // 첫 번째 포인트 추가
            val firstValue = daysData.first()
            val firstY = height - (((firstValue - minValue) / adjustedValueRange) * height)
            add(Offset(currentX, firstY))

            // 나머지 포인트들 추가
            timeIntervals.forEachIndexed { index, interval ->
                val ratio = interval / totalInterval
                currentX += chartWidth * ratio
                val value = daysData[index + 1]
                val y = height - (((value - minValue) / adjustedValueRange) * height)
                add(Offset(currentX, y))
            }
        }

        // 3등분 수평 점선 그리기
        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(
                ResetRecordChartDefaults.GRID_DASH_SIZE,
                ResetRecordChartDefaults.GRID_DASH_INTERVAL,
            ),
            0f,
        )

        for (i in 0..GRID_LINE_COUNT) {
            val y = height * i / GRID_LINE_COUNT

            // 수치 텍스트 그리기
            val value = (maxValue - (adjustedValueRange * i / GRID_LINE_COUNT)).coerceAtLeast(0f)
            val scaleText = "%.1f %s".format(value, valueSuffix)

            drawContext.canvas.nativeCanvas.drawText(
                scaleText,
                maxScaleTextWidth,
                y + scaleTextPaint.textSize / TEXT_BASELINE_ADJUSTMENT,
                scaleTextPaint,
            )

            // 수평 점선 그리기
            drawLine(
                color = Color.LightGray,
                start = Offset(chartStartX, y),
                end = Offset(size.width, y),
                strokeWidth = GRID_LINE_STROKE_WIDTH.dp.toPx(),
                pathEffect = pathEffect,
            )
        }

        // 곡선 데이터 라인 그리기
        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        for (i in 1 until points.size) {
            val previousPoint = points[i - 1]
            val currentPoint = points[i]

            val controlX1 = previousPoint.x + (currentPoint.x - previousPoint.x) / 2
            val controlX2 = previousPoint.x + (currentPoint.x - previousPoint.x) / 2

            path.cubicTo(
                controlX1,
                previousPoint.y,
                controlX2,
                currentPoint.y,
                currentPoint.x,
                currentPoint.y,
            )
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = lineWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )

        // 날짜 텍스트 그리기
        val dateY = size.height - BOTTOM_TEXT_BASELINE_PADDING.dp.toPx()

        // 날짜 표시 간격 계산 (시간 간격 기반)
        val dateDisplayIndexes = mutableListOf<Int>()

        // 첫 번째와 마지막 인덱스는 무조건 추가
        dateDisplayIndexes.add(0)
        dateDisplayIndexes.add(points.lastIndex)

        // 중간 점들에 대해 간격 체크 (양 끝점과의 간격을 모두 고려)
        for (index in 1 until points.lastIndex) {
            val point = points[index]
            val prevDisplayedPoint = points[dateDisplayIndexes.maxOf { if (it < index) it else 0 }]
            val nextDisplayedPoint = points[dateDisplayIndexes.minOf { if (it > index) it else points.lastIndex }]

            // 이전 표시된 점과 다음 표시될 점 모두와 충분한 간격이 있는 경우만 추가
            if (point.x - prevDisplayedPoint.x >= dateSpacing.toPx() &&
                nextDisplayedPoint.x - point.x >= dateSpacing.toPx()
            ) {
                dateDisplayIndexes.add(index)
            }
        }

        // 인덱스 순서대로 정렬
        dateDisplayIndexes.sort()

        // 하단 날짜들 그리기
        points.forEachIndexed { index, point ->
            if (index in dateDisplayIndexes && index != selectedIndex) {
                drawContext.canvas.nativeCanvas.drawText(
                    dates[index],
                    point.x,
                    dateY,
                    dateTextPaint,
                )
            }
        }

        // 선택된 포인트의 수직 점선을 애니메이션된 위치에 그리기
        selectedIndex
            ?.takeIf { it in daysData.indices }
            ?.let {
                val verticalPathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(
                        ResetRecordChartDefaults.SELECTION_DASH_SIZE,
                        ResetRecordChartDefaults.SELECTION_DASH_INTERVAL,
                    ),
                    0f,
                )

                drawLine(
                    color = lineColor.copy(alpha = SELECTION_LINE_ALPHA),
                    start = Offset(selectedPoint.x, 0f),
                    end = Offset(selectedPoint.x, height),
                    strokeWidth = SELECTION_LINE_STROKE_WIDTH.dp.toPx(),
                    pathEffect = verticalPathEffect,
                )

                // 상단 값 표시 박스
                val value = daysData[it]
                val text = "%.1f".format(value)

                val textBounds = Rect()
                valueTextPaint.getTextBounds(text, 0, text.length, textBounds)

                val rectPadding = valueBoxPadding.toPx()
                val rectWidth = textBounds.width() + rectPadding * 2
                val rectHeight = textBounds.height() + rectPadding * 2

                val rectTop = -rectHeight - VALUE_BOX_TOP_PADDING.dp.toPx()
                val rectLeft = selectedPoint.x - rectWidth / 2

                drawRoundRect(
                    color = valueBoxBackgroundColor,
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(VALUE_BOX_CORNER_RADIUS.dp.toPx()),
                )

                drawContext.canvas.nativeCanvas.drawText(
                    text,
                    selectedPoint.x,
                    rectTop + rectHeight - rectPadding,
                    valueTextPaint,
                )

                // 도넛 뒤의 리플 효과
                drawCircle(
                    color = lineColor.copy(alpha = rippleAlpha),
                    radius = endPointRadius.toPx() * rippleScale,
                    center = selectedPoint,
                    style = Stroke(width = lineWidth.toPx() / 2),
                )

                // 도넛 모양
                drawCircle(
                    color = donutBackgroundColor,
                    radius = endPointRadius.toPx(),
                    center = selectedPoint,
                )

                drawCircle(
                    color = lineColor,
                    radius = endPointRadius.toPx(),
                    center = selectedPoint,
                    style = Stroke(width = lineWidth.toPx()),
                )

                // 하단 날짜 박스
                val selectedDate = data[it]
                val dateText = if (selectedDate.year == currentYear) {
                    selectedDate.format(normalDateFormatter)
                } else {
                    selectedDate.format(selectedDateFormatter)
                }

                val dateTextBounds = Rect()
                dateTextPaint.getTextBounds(dateText, 0, dateText.length, dateTextBounds)

                val dateRectPadding = dateBoxPadding.toPx()
                val dateRectWidth = dateTextBounds.width() + dateRectPadding * 2
                val dateRectHeight = dateTextBounds.height() + dateRectPadding * 2
                val dateRectLeft = selectedPoint.x - dateRectWidth / 2
                val dateRectTop = dateY - dateTextBounds.height() - dateRectPadding

                drawRoundRect(
                    color = dateBoxBackgroundColor,
                    topLeft = Offset(dateRectLeft, dateRectTop),
                    size = Size(dateRectWidth, dateRectHeight),
                    cornerRadius = CornerRadius(DATE_BOX_CORNER_RADIUS.dp.toPx()),
                )

                drawContext.canvas.nativeCanvas.drawText(
                    dateText,
                    selectedPoint.x,
                    dateY,
                    Paint().apply {
                        textSize = dateTextStyle.fontSize.toPx()
                        textAlign = Paint.Align.CENTER
                        color = dateBoxTextColor.toArgb()
                        typeface = dateTextPaint.typeface
                    },
                )
            }
    }
}

private fun <T> List<T>.indexOfMinBy(selector: (T) -> Float): Int? {
    if (isEmpty()) return null
    var minValue = selector(get(0))
    var minIndex = 0
    for (i in 1..lastIndex) {
        val value = selector(get(i))
        if (value < minValue) {
            minValue = value
            minIndex = i
        }
    }
    return minIndex
}
