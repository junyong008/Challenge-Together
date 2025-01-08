package com.yjy.feature.addchallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.to12HourFormat
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.Calendar
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.SelectionMode
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TimePicker
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
internal fun SetStartDateRoute(
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetStartDateScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onContinue = onContinue,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun SetStartDateScreen(
    modifier: Modifier = Modifier,
    uiState: AddChallengeUiState = AddChallengeUiState(),
    uiEvent: Flow<AddChallengeUiEvent> = flowOf(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinue: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val startDateOutOfRangeMessage = stringResource(id = R.string.feature_addchallenge_invalid_start_date)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            AddChallengeUiEvent.StartDateTimeOutOfRange ->
                onShowSnackbar(SnackbarType.ERROR, startDateOutOfRangeMessage)

            else -> Unit
        }
    }

    Scaffold(
        bottomBar = {
            ChallengeTogetherBottomAppBar(
                onBackClick = onBackClick,
                onContinueClick = onContinue,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TitleWithDescription(
                titleRes = R.string.feature_addchallenge_title_set_start_date,
                descriptionRes = R.string.feature_addchallenge_description_set_start_date,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                DateSelector(
                    selectedDate = uiState.startDateTime.toLocalDate(),
                    onDateSelected = { selectedDate ->
                        val (hour, minute, isAm) = uiState.startDateTime.to12HourFormat()
                        processAction(AddChallengeUiAction.OnStartDateTimeUpdated(selectedDate, hour, minute, isAm))
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeSelector(
                    startDateTime = uiState.startDateTime,
                    onTimeSelected = { selectedHour, selectedMinute, selectedAmPm ->
                        val selectedDate = uiState.startDateTime.toLocalDate()
                        processAction(
                            AddChallengeUiAction.OnStartDateTimeUpdated(
                                selectedDate = selectedDate,
                                hour = selectedHour,
                                minute = selectedMinute,
                                isAm = selectedAmPm,
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    Calendar(
        selectionMode = SelectionMode.SingleDate(selectedDate),
        onDateSelected = onDateSelected,
        maxDate = LocalDate.now(),
        showAdjacentMonthsDays = false,
        enableWeekModeOnDataSelected = true,
    )
}

@Composable
private fun TimeSelector(
    startDateTime: LocalDateTime,
    onTimeSelected: (Int, Int, Boolean) -> Unit,
) {
    val (hour, minute, isAm) = startDateTime.to12HourFormat()
    TimePicker(
        hour = hour,
        minute = minute,
        isAm = isAm,
        onTimeChanged = onTimeSelected,
    )
}

@ThemePreviews
@Composable
fun SetStartDateScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SetStartDateScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
