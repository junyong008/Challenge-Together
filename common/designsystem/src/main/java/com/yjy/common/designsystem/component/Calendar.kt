package com.yjy.common.designsystem.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.core.constants.TimeConst.DAYS_PER_WEEK
import com.yjy.common.core.constants.TimeConst.MONTHS_PER_YEAR
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

private const val DEFAULT_MIN_YEAR = 1900
private const val DEFAULT_MIN_MONTH = 1
private const val DEFAULT_MIN_DAY = 1
private const val DEFAULT_MAX_YEAR = 2100
private const val DEFAULT_MAX_MONTH = 12
private const val DEFAULT_MAX_DAY = 31

/**
 * [Calendar]
 *
 * - 챌린지 생성 시 시작 날짜 선택, 진행중인 챌린지의 날짜 범위 표기를 위한 캘린더.
 * - 광범위한 커스텀을 위해 서드 파티 라이브러리를 사용하지 않고 직접 구현.
 *
 * @param selectionMode 단일 날짜 선택 모드와 범위를 선택 가능
 * @param onDateSelected 사용자가 클릭한 날짜
 * @param showAdjacentMonthsDays 이전/다음 달의 일자도 희미하게 표시할지 여부
 * @param enableWeekModeOnDataSelected 날짜를 클릭하면 주간 모드로 전환할지 여부
 * @param minDate 선택 가능한 최소 날짜
 * @param maxDate 선택 가능한 최대 날짜
 */
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    selectionMode: SelectionMode = SelectionMode.SingleDate(),
    onDateSelected: (LocalDate) -> Unit = {},
    showAdjacentMonthsDays: Boolean = false,
    enableWeekModeOnDataSelected: Boolean = false,
    minDate: LocalDate = LocalDate.of(DEFAULT_MIN_YEAR, DEFAULT_MIN_MONTH, DEFAULT_MIN_DAY),
    maxDate: LocalDate = LocalDate.of(DEFAULT_MAX_YEAR, DEFAULT_MAX_MONTH, DEFAULT_MAX_DAY),
    calendarColors: CalendarColors = CalendarColors(
        containerColor = CustomColorProvider.colorScheme.surface,
        contentColor = CustomColorProvider.colorScheme.onSurface,
        weekDayColor = CustomColorProvider.colorScheme.onSurfaceMuted,
        selectedBackgroundColor = CustomColorProvider.colorScheme.brand,
        selectedTextColor = CustomColorProvider.colorScheme.onBrand,
        rangeBackgroundColor = CustomColorProvider.colorScheme.brandBright,
        rangeTextColor = CustomColorProvider.colorScheme.onBrandBright,
        disabledColor = CustomColorProvider.colorScheme.disable,
        dividerColor = CustomColorProvider.colorScheme.divider,
    ),
) {
    // 주간 모드를 활성화 할지 여부. 주간 모드는 선택된 날짜의 주만 표기하는 모드.
    var weekMode by remember { mutableStateOf(enableWeekModeOnDataSelected) }

    // 아래 LaunchedEffect에서 사용자가 페이지를 이동하면 무조건 주간 모드를 해지하는데, 초기의 주간 모드를 유지하기 위한 변수.
    var isInitialLoad by remember { mutableStateOf(true) }

    val selectedDate = when (selectionMode) {
        is SelectionMode.SingleDate -> selectionMode.selectedDate
        is SelectionMode.DateRange -> selectionMode.endDate ?: LocalDate.now()
    }

    val initialYearMonth = YearMonth.from(selectedDate ?: LocalDate.now())
    val coroutineScope = rememberCoroutineScope()

    // pagerState는 달력을 슬라이드로 넘기는 상태를 관리하는데 사용되며, 초기 페이지를 상단의 선택된 날짜가 있는 달로 설정.
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2 + monthsBetween(YearMonth.now(), initialYearMonth),
        pageCount = { Int.MAX_VALUE },
    )

    // 페이지 이동 감지시 주간 모드를 해제. 초기에 주간 모드로 보여 주고 싶기에 초기 값을 유지.
    LaunchedEffect(pagerState.currentPage) {
        if (!isInitialLoad) {
            weekMode = false
        }
        isInitialLoad = false
    }

    // 주간 모드가 활성화 된 경우 선택된 날짜가 있는 달로 페이지를 이동.
    LaunchedEffect(weekMode) {
        if (weekMode && selectedDate != null) {
            val monthsSinceInitial = monthsBetween(YearMonth.now(), initialYearMonth)
            val midPoint = Int.MAX_VALUE / 2
            val monthsToAdd = pagerState.currentPage - (midPoint + monthsSinceInitial)

            val selectedYearMonth = YearMonth.from(selectedDate)
            val currentYearMonth = initialYearMonth.plusMonths(monthsToAdd.toLong())

            if (selectedYearMonth != currentYearMonth) {
                val targetPage = pagerState.currentPage + monthsBetween(currentYearMonth, selectedYearMonth)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(targetPage)
                }
            }
        }
    }

    // 첫 번째 요일을 기준으로 요일 리스트를 생성.
    val daysOfWeek = remember {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        DayOfWeek.entries.let { days ->
            val firstDayIndex = days.indexOf(firstDayOfWeek)
            (days.subList(firstDayIndex, days.size) + days.subList(0, firstDayIndex))
        }
    }

    // 주 모드 전환 시 높이 변화에 따른 애니메이션 효과 적용.
    val calendarHeight by animateDpAsState(
        targetValue = if (weekMode) 50.dp else 195.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "WeekMode Animation",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(calendarColors.containerColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 보여지는 페이지에 따른 년도와 월 계산.
        val displayedYearMonth = remember(pagerState.currentPage) {
            val monthsSinceInitial = monthsBetween(YearMonth.now(), initialYearMonth)
            val midPoint = Int.MAX_VALUE / 2
            val monthsToAdd = pagerState.currentPage - (midPoint + monthsSinceInitial)

            initialYearMonth.plusMonths(monthsToAdd.toLong())
        }

        // 선택 가능한 최대 날짜, 최소 날짜에 따라 헤더의 좌우 버튼 비 활성화 유무 결정
        val canMoveToPreviousMonth = displayedYearMonth.isAfter(YearMonth.from(minDate))
        val canMoveToNextMonth = displayedYearMonth.isBefore(YearMonth.from(maxDate))

        // modifier의 padding이 일정하지 않은 이유: [IconButton], [년도 월]의 선택 가능한 padding과 시각적 padding의 차이.
        CalendarHeader(
            currentYearMonth = displayedYearMonth,
            onPreviousMonth = {
                if (canMoveToPreviousMonth) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            },
            onNextMonth = {
                if (canMoveToNextMonth) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            onYearMonthChange = { newYearMonth ->
                coroutineScope.launch {
                    val newPage = Int.MAX_VALUE / 2 + monthsBetween(YearMonth.now(), newYearMonth)
                    pagerState.scrollToPage(newPage)
                }
            },
            isWeekMode = weekMode,
            onDisableWeekMode = { weekMode = false },
            canMoveToPreviousMonth = canMoveToPreviousMonth,
            canMoveToNextMonth = canMoveToNextMonth,
            minDate = minDate,
            maxDate = maxDate,
            contentColor = calendarColors.contentColor,
            disabledColor = calendarColors.disabledColor,
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 16.dp, bottom = 16.dp),
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = calendarColors.dividerColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        CalendarWeekDays(
            daysOfWeek = daysOfWeek,
            weekDayColor = calendarColors.weekDayColor,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(calendarHeight),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val currentDisplayedYearMonth =
                    initialYearMonth.plusMonths(
                        (page - (Int.MAX_VALUE / 2 + monthsBetween(YearMonth.now(), initialYearMonth))).toLong(),
                    )

                CalendarDays(
                    currentYearMonth = currentDisplayedYearMonth,
                    selectionMode = selectionMode,
                    onDateSelected = {
                        onDateSelected(it)
                        if (enableWeekModeOnDataSelected) {
                            weekMode = true
                        }
                    },
                    showAdjacentMonthsDays = showAdjacentMonthsDays,
                    daysOfWeek = daysOfWeek,
                    isWeekMode = weekMode,
                    selectedDate = selectedDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    calendarColors = calendarColors,
                )
            }
        }
    }
}

@Composable
fun CalendarHeader(
    currentYearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onYearMonthChange: (YearMonth) -> Unit,
    isWeekMode: Boolean,
    onDisableWeekMode: () -> Unit,
    canMoveToPreviousMonth: Boolean,
    canMoveToNextMonth: Boolean,
    minDate: LocalDate,
    maxDate: LocalDate,
    contentColor: Color,
    disabledColor: Color,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(
                id = R.string.common_designsystem_calendar_year_month,
                currentYearMonth.year,
                currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            ),
            color = contentColor,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .clickable { showDialog = true }
                .padding(8.dp),
        )

        if (showDialog) {
            YearMonthPickerDialog(
                initialYear = currentYearMonth.year,
                initialMonth = currentYearMonth.month.value,
                onDismissRequest = { showDialog = false },
                onConfirm = { year, month ->
                    onYearMonthChange(YearMonth.of(year, month))
                    showDialog = false
                },
                minDate = minDate,
                maxDate = maxDate,
            )
        }

        Row {
            if (isWeekMode) {
                IconButton(onClick = onDisableWeekMode) {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.ArrowDown),
                        contentDescription = stringResource(
                            id = R.string.common_designsystem_calendar_expand_week_mode,
                        ),
                        tint = contentColor,
                    )
                }
            } else {
                IconButton(
                    onClick = onPreviousMonth,
                    enabled = canMoveToPreviousMonth,
                ) {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.ArrowLeft),
                        contentDescription = stringResource(id = R.string.common_designsystem_calendar_previous_month),
                        tint = if (canMoveToPreviousMonth) {
                            contentColor
                        } else {
                            disabledColor
                        },
                    )
                }
                IconButton(
                    onClick = onNextMonth,
                    enabled = canMoveToNextMonth,
                ) {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.ArrowRight),
                        contentDescription = stringResource(id = R.string.common_designsystem_calendar_next_month),
                        tint = if (canMoveToNextMonth) {
                            contentColor
                        } else {
                            disabledColor
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarWeekDays(
    daysOfWeek: List<DayOfWeek>,
    weekDayColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    textAlign = TextAlign.Center,
                    color = weekDayColor,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun CalendarDays(
    currentYearMonth: YearMonth,
    selectionMode: SelectionMode,
    onDateSelected: (LocalDate) -> Unit,
    showAdjacentMonthsDays: Boolean,
    daysOfWeek: List<DayOfWeek>,
    isWeekMode: Boolean?,
    selectedDate: LocalDate?,
    minDate: LocalDate,
    maxDate: LocalDate,
    calendarColors: CalendarColors,
    modifier: Modifier = Modifier,
) {
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val lastDayOfMonth = currentYearMonth.atEndOfMonth()
    val firstDayOfWeek = daysOfWeek.first()

    // 월의 첫번째 주의 첫번째 날짜와 월의 마지막 주의 마지막 날짜를 구한다.
    val firstDayOfGrid = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val lastDayOfGrid = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(daysOfWeek.last()))

    // 첫번째 날과 마지막 날의 차이를 통해 월의 총 일수를 계산, 리스트 생성.
    val totalDays = ChronoUnit.DAYS.between(firstDayOfGrid, lastDayOfGrid) + 1
    val dates = (0 until totalDays.toInt()).map { firstDayOfGrid.plusDays(it.toLong()) }

    // 날짜 리스트를 CalendarDayData로 변환하고, 현재 월에 포함된 날짜인지 유무도 같이 저장.
    val dayDataList = dates.map { date ->
        CalendarDayData(
            date = date,
            isCurrentMonth = date.month == currentYearMonth.month,
        )
    }

    // 주간 모드인 경우 선택된 날짜가 포함된 주만 표기하도록 filter.
    val weeks = if (isWeekMode == true && selectedDate != null) {
        dayDataList.chunked(DAYS_PER_WEEK).filter { week -> week.any { it.date == selectedDate } }
    } else {
        dayDataList.chunked(DAYS_PER_WEEK)
    }

    Column(modifier = modifier) {
        weeks.forEach { week ->

            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { dayData ->

                    val isAdjacentMonth = !dayData.isCurrentMonth
                    val isEnabled = dayData.date.isAfter(minDate.minusDays(1)) &&
                        dayData.date.isBefore(maxDate.plusDays(1))

                    if (showAdjacentMonthsDays || dayData.isCurrentMonth) {
                        CalendarDay(
                            date = dayData.date,
                            selectionMode = selectionMode,
                            isAdjacentMonth = isAdjacentMonth,
                            onClick = { if (isEnabled) onDateSelected(dayData.date) },
                            isEnabled = isEnabled,
                            calendarColors = calendarColors,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate,
    selectionMode: SelectionMode,
    isAdjacentMonth: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean,
    calendarColors: CalendarColors,
    modifier: Modifier = Modifier,
) {
    val isSelected = when (selectionMode) {
        is SelectionMode.SingleDate -> date == selectionMode.selectedDate
        is SelectionMode.DateRange -> date == selectionMode.endDate
    }

    val isInRange = when (selectionMode) {
        is SelectionMode.SingleDate -> false
        is SelectionMode.DateRange -> {
            val start = selectionMode.startDate
            val end = selectionMode.endDate
            if (start != null && end != null) {
                (date.isAfter(start) && date.isBefore(end)) || date == start || date == end
            } else {
                false
            }
        }
    }

    val backgroundColor = when {
        isInRange -> calendarColors.rangeBackgroundColor
        else -> Color.Transparent
    }

    val backgroundShape = when {
        selectionMode is SelectionMode.DateRange && date == selectionMode.startDate ->
            RoundedCornerShape(
                topStart = 50.dp,
                bottomStart = 50.dp,
            )

        selectionMode is SelectionMode.DateRange && date == selectionMode.endDate ->
            RoundedCornerShape(
                topEnd = 50.dp,
                bottomEnd = 50.dp,
            )

        isInRange -> RectangleShape
        else -> MaterialTheme.shapes.large
    }

    val textStyle = when {
        isSelected -> MaterialTheme.typography.bodyMedium
        else -> MaterialTheme.typography.labelMedium
    }

    val textColor = when {
        !isEnabled -> calendarColors.disabledColor
        isSelected -> calendarColors.selectedTextColor
        isInRange && isAdjacentMonth -> calendarColors.rangeTextColor.copy(alpha = 0.3f)
        isInRange -> calendarColors.rangeTextColor
        isAdjacentMonth -> calendarColors.contentColor.copy(alpha = 0.3f)
        else -> calendarColors.contentColor
    }

    Box(
        modifier = modifier
            .height(40.dp)
            .padding(vertical = 3.dp)
            .background(
                color = backgroundColor,
                shape = backgroundShape,
            )
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(color = calendarColors.selectedBackgroundColor),
            )
        }
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
            style = textStyle,
        )
    }
}

sealed class SelectionMode {
    data class SingleDate(val selectedDate: LocalDate? = null) : SelectionMode()
    data class DateRange(val startDate: LocalDate? = null, val endDate: LocalDate? = null) : SelectionMode()
}

data class CalendarColors(
    val containerColor: Color,
    val contentColor: Color,
    val weekDayColor: Color,
    val selectedBackgroundColor: Color,
    val selectedTextColor: Color,
    val rangeBackgroundColor: Color,
    val rangeTextColor: Color,
    val disabledColor: Color,
    val dividerColor: Color,
)

data class CalendarDayData(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
)

private fun monthsBetween(from: YearMonth, to: YearMonth): Int {
    val yearDifference = to.year - from.year
    val monthDifference = to.monthValue - from.monthValue
    return yearDifference * MONTHS_PER_YEAR + monthDifference
}

@ComponentPreviews
@Composable
fun CalendarPreview() {
    ChallengeTogetherTheme {
        Calendar(showAdjacentMonthsDays = true)
    }
}
