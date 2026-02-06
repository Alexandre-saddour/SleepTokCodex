package com.example.kmpbackbone.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.model.WeeklyRecap
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonCard
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.theme.NeonColors
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
import kmpbackbone.composeapp.generated.resources.duration_hours
import kmpbackbone.composeapp.generated.resources.duration_hours_minutes
import kmpbackbone.composeapp.generated.resources.duration_minutes
import kmpbackbone.composeapp.generated.resources.progress_detail_close
import kmpbackbone.composeapp.generated.resources.progress_detail_not_available
import kmpbackbone.composeapp.generated.resources.progress_detail_plan_vs_actual
import kmpbackbone.composeapp.generated.resources.progress_detail_score
import kmpbackbone.composeapp.generated.resources.progress_detail_status
import kmpbackbone.composeapp.generated.resources.progress_detail_title
import kmpbackbone.composeapp.generated.resources.progress_detail_xp
import kmpbackbone.composeapp.generated.resources.progress_legend_fail
import kmpbackbone.composeapp.generated.resources.progress_legend_partial
import kmpbackbone.composeapp.generated.resources.progress_legend_success
import kmpbackbone.composeapp.generated.resources.progress_loading
import kmpbackbone.composeapp.generated.resources.progress_month_label
import kmpbackbone.composeapp.generated.resources.progress_next_month
import kmpbackbone.composeapp.generated.resources.progress_previous_month
import kmpbackbone.composeapp.generated.resources.progress_status_fail
import kmpbackbone.composeapp.generated.resources.progress_status_in_progress
import kmpbackbone.composeapp.generated.resources.progress_status_partial
import kmpbackbone.composeapp.generated.resources.progress_status_success
import kmpbackbone.composeapp.generated.resources.progress_title
import kmpbackbone.composeapp.generated.resources.progress_weekly_avg_score
import kmpbackbone.composeapp.generated.resources.progress_weekly_best_streak
import kmpbackbone.composeapp.generated.resources.progress_weekly_sleep_gained
import kmpbackbone.composeapp.generated.resources.progress_weekly_slept
import kmpbackbone.composeapp.generated.resources.progress_weekly_title
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val weekdayLabels = listOf(
    Res.string.day_mon_short,
    Res.string.day_tue_short,
    Res.string.day_wed_short,
    Res.string.day_thu_short,
    Res.string.day_fri_short,
    Res.string.day_sat_short,
    Res.string.day_sun_short,
)

private data class LegendEntry(
    val status: NightStatus,
    val labelRes: StringResource,
    val color: Color,
)

private val legendEntries = listOf(
    LegendEntry(NightStatus.SUCCESS, Res.string.progress_legend_success, NeonColors.NeonGreen),
    LegendEntry(NightStatus.PARTIAL, Res.string.progress_legend_partial, NeonColors.StatusPartial),
    LegendEntry(NightStatus.FAIL, Res.string.progress_legend_fail, NeonColors.StatusFail),
)

@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onDismissDetail: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NeonGradientBackground(
            modifier = Modifier.padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.progress_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = NeonColors.TextPrimary,
                )
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = NeonColors.NeonElectricBlue,
                    )
                    Text(
                        text = stringResource(Res.string.progress_loading),
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonColors.TextSecondary,
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
                    if (uiState.showWeeklyRecap) {
                        uiState.weeklyRecap?.let { WeeklyRecapCard(it) }
                    }
                }
            }
        }
        if (uiState.selectedNight != null) {
            NightDetailOverlay(
                night = uiState.selectedNight,
                onDismiss = onDismissDetail,
            )
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
        NeonNavButton(
            text = stringResource(Res.string.progress_previous_month),
            onClick = onPreviousMonth,
        )
        Text(
            text = stringResource(
                Res.string.progress_month_label,
                monthStart.year,
                monthStart.monthNumber,
            ),
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )
        NeonNavButton(
            text = stringResource(Res.string.progress_next_month),
            onClick = onNextMonth,
        )
    }
}

@Composable
private fun NeonNavButton(
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = NeonColors.NeonElectricBlue.copy(alpha = 0.1f),
        contentColor = NeonColors.NeonElectricBlue,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun CalendarHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        weekdayLabels.forEach { label ->
            Text(
                text = stringResource(label),
                style = MaterialTheme.typography.labelLarge,
                color = NeonColors.TextSecondary,
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
                    NeonCalendarCell(
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
private fun NeonCalendarCell(
    day: CalendarDayUi?,
    showAdvanced: Boolean,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier,
) {
    if (day == null) {
        Box(
            modifier = modifier.height(54.dp),
        )
        return
    }

    val statusColor = statusColor(day.status)
    val backgroundColor = statusColor?.copy(alpha = 0.15f) ?: NeonColors.NeonDarkSurfaceVariant
    val borderColor = statusColor?.copy(alpha = 0.5f) ?: NeonColors.Outline
    val textColor = statusColor ?: NeonColors.TextPrimary

    Surface(
        modifier = modifier
            .height(54.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.small,
            ),
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        onClick = { if (day.nightId != null) onDaySelected(day.date) },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
            )
            if (showAdvanced && day.score != null) {
                Text(
                    text = day.score.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.8f),
                )
            }
        }
    }
}

private fun statusColor(status: NightStatus?): Color? {
    return when (status) {
        NightStatus.SUCCESS -> NeonColors.NeonGreen
        NightStatus.PARTIAL -> NeonColors.StatusPartial
        NightStatus.FAIL -> NeonColors.StatusFail
        else -> null
    }
}

@Composable
private fun LegendRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        legendEntries.forEach { entry ->
            LegendItem(
                color = entry.color,
                label = stringResource(entry.labelRes),
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color.copy(alpha = 0.3f), shape = MaterialTheme.shapes.small)
                .border(1.dp, color.copy(alpha = 0.6f), MaterialTheme.shapes.small),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = NeonColors.TextSecondary,
        )
    }
}

@Composable
private fun WeeklyRecapCard(recap: WeeklyRecap) {
    NeonCard(
        glowColor = NeonColors.NeonPurple,
        glowIntensity = 0.25f,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.progress_weekly_title),
                style = MaterialTheme.typography.titleLarge,
                color = NeonColors.NeonPurple,
            )
            RecapStat(
                text = stringResource(
                    Res.string.progress_weekly_slept,
                    formatMinutes(recap.totalSleptMinutes),
                    formatMinutes(recap.targetMinutes),
                ),
            )
            RecapStat(
                text = stringResource(
                    Res.string.progress_weekly_sleep_gained,
                    recap.sleepGainedMinutes,
                ),
            )
            RecapStat(
                text = stringResource(
                    Res.string.progress_weekly_best_streak,
                    recap.bestStreak,
                ),
            )
            RecapStat(
                text = stringResource(
                    Res.string.progress_weekly_avg_score,
                    recap.averageScore,
                ),
            )
        }
    }
}

@Composable
private fun RecapStat(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = NeonColors.TextSecondary,
    )
}

@Composable
private fun formatMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> stringResource(
            Res.string.duration_hours_minutes,
            hours,
            minutes,
        )
        hours > 0 -> stringResource(Res.string.duration_hours, hours)
        else -> stringResource(Res.string.duration_minutes, minutes)
    }
}

@Composable
private fun NightDetailOverlay(
    night: Night,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.NeonDarkBackground.copy(alpha = 0.9f))
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        NeonCard(
            glowColor = statusColor(night.status) ?: NeonColors.NeonElectricBlue,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(Res.string.progress_detail_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonColors.TextPrimary,
                )
                Text(
                    text = stringResource(
                        Res.string.progress_detail_plan_vs_actual,
                        formatMinutes(night.planDurationMinutes),
                        night.actualDurationMinutes?.let { formatMinutes(it) }
                            ?: stringResource(Res.string.progress_detail_not_available),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonColors.TextSecondary,
                )
                night.score?.let { score ->
                    Text(
                        text = stringResource(Res.string.progress_detail_score, score),
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonColors.NeonYellow,
                    )
                }
                night.xpEarned?.let { xp ->
                    Text(
                        text = stringResource(Res.string.progress_detail_xp, xp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonColors.NeonGreen,
                    )
                }
                Text(
                    text = stringResource(
                        Res.string.progress_detail_status,
                        statusLabel(night.status),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColor(night.status) ?: NeonColors.TextSecondary,
                )
                NeonButton(
                    onClick = onDismiss,
                    glowColor = NeonColors.NeonElectricBlue,
                ) {
                    Text(
                        text = stringResource(Res.string.progress_detail_close),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun statusLabel(status: NightStatus): String {
    return when (status) {
        NightStatus.SUCCESS -> stringResource(Res.string.progress_status_success)
        NightStatus.PARTIAL -> stringResource(Res.string.progress_status_partial)
        NightStatus.FAIL -> stringResource(Res.string.progress_status_fail)
        NightStatus.IN_PROGRESS -> stringResource(Res.string.progress_status_in_progress)
        NightStatus.VOID -> stringResource(Res.string.progress_status_fail)
    }
}
