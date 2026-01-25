package com.example.kmpbackbone.ui.progress

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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.NightStatus
import com.example.kmpbackbone.viewmodel.CalendarDayUi
import com.example.kmpbackbone.viewmodel.ProgressUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.day_fri_short
import kmpbackbone.composeapp.generated.resources.day_mon_short
import kmpbackbone.composeapp.generated.resources.day_sat_short
import kmpbackbone.composeapp.generated.resources.day_sun_short
import kmpbackbone.composeapp.generated.resources.day_thu_short
import kmpbackbone.composeapp.generated.resources.day_tue_short
import kmpbackbone.composeapp.generated.resources.day_wed_short
import kmpbackbone.composeapp.generated.resources.progress_loading
import kmpbackbone.composeapp.generated.resources.progress_month_label
import kmpbackbone.composeapp.generated.resources.progress_next_month
import kmpbackbone.composeapp.generated.resources.progress_previous_month
import kmpbackbone.composeapp.generated.resources.progress_legend_fail
import kmpbackbone.composeapp.generated.resources.progress_legend_partial
import kmpbackbone.composeapp.generated.resources.progress_legend_success
import kmpbackbone.composeapp.generated.resources.progress_title
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onDismissDetail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.progress_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text(
                text = stringResource(Res.string.progress_loading),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else if (uiState.monthStart != null) {
            MonthSelector(
                monthStart = uiState.monthStart,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
            )
            CalendarHeader()
            CalendarGrid(
                monthStart = uiState.monthStart,
                days = uiState.days,
                showAdvanced = uiState.showAdvancedCalendar,
                onDaySelected = onDaySelected,
            )
            LegendRow()
        }
    }
}

@Composable
private fun MonthSelector(
    monthStart: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = onPreviousMonth) {
            Text(text = stringResource(Res.string.progress_previous_month))
        }
        Text(
            text = stringResource(
                Res.string.progress_month_label,
                monthStart.year,
                monthStart.monthNumber,
            ),
            style = MaterialTheme.typography.titleLarge,
        )
        Button(onClick = onNextMonth) {
            Text(text = stringResource(Res.string.progress_next_month))
        }
    }
}

@Composable
private fun CalendarHeader() {
    val labels = listOf(
        Res.string.day_mon_short,
        Res.string.day_tue_short,
        Res.string.day_wed_short,
        Res.string.day_thu_short,
        Res.string.day_fri_short,
        Res.string.day_sat_short,
        Res.string.day_sun_short,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        labels.forEach { label ->
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    monthStart: LocalDate,
    days: List<CalendarDayUi>,
    showAdvanced: Boolean,
    onDaySelected: (LocalDate) -> Unit,
) {
    val offset = (monthStart.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal + 7) % 7
    val cells = List(offset) { null } + days
    val rows = cells.chunked(7)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { day ->
                    CalendarCell(
                        day = day,
                        showAdvanced = showAdvanced,
                        onDaySelected = onDaySelected,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size < 7) {
                    repeat(7 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(
    day: CalendarDayUi?,
    showAdvanced: Boolean,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier,
) {
    if (day == null) {
        Box(
            modifier = modifier
                .height(54.dp),
        )
        return
    }
    val statusColor = when (day.status) {
        NightStatus.SUCCESS -> MaterialTheme.colorScheme.secondary
        NightStatus.PARTIAL -> MaterialTheme.colorScheme.tertiary
        NightStatus.FAIL -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (day.status) {
        NightStatus.SUCCESS -> MaterialTheme.colorScheme.onSecondary
        NightStatus.PARTIAL -> MaterialTheme.colorScheme.onTertiary
        NightStatus.FAIL -> MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = statusColor,
        contentColor = contentColor,
        modifier = modifier
            .height(54.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = day.nightId != null) { onDaySelected(day.date) },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = day.date.dayOfMonth.toString())
            if (showAdvanced && day.score != null) {
                Text(
                    text = day.score.toString(),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun LegendRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LegendItem(
            color = MaterialTheme.colorScheme.secondary,
            label = stringResource(Res.string.progress_legend_success),
        )
        LegendItem(
            color = MaterialTheme.colorScheme.tertiary,
            label = stringResource(Res.string.progress_legend_partial),
        )
        LegendItem(
            color = MaterialTheme.colorScheme.error,
            label = stringResource(Res.string.progress_legend_fail),
        )
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = MaterialTheme.shapes.small),
        )
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}
