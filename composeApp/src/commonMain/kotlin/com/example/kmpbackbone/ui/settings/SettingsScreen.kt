package com.example.kmpbackbone.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.CoachStyle
import com.example.domain.result.DomainError
import com.example.kmpbackbone.viewmodel.SettingsUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.coach_chill_sample
import kmpbackbone.composeapp.generated.resources.coach_chill_title
import kmpbackbone.composeapp.generated.resources.coach_hype_sample
import kmpbackbone.composeapp.generated.resources.coach_hype_title
import kmpbackbone.composeapp.generated.resources.coach_strict_sample
import kmpbackbone.composeapp.generated.resources.coach_strict_title
import kmpbackbone.composeapp.generated.resources.day_fri_short
import kmpbackbone.composeapp.generated.resources.day_mon_short
import kmpbackbone.composeapp.generated.resources.day_sat_short
import kmpbackbone.composeapp.generated.resources.day_sun_short
import kmpbackbone.composeapp.generated.resources.day_thu_short
import kmpbackbone.composeapp.generated.resources.day_tue_short
import kmpbackbone.composeapp.generated.resources.day_wed_short
import kmpbackbone.composeapp.generated.resources.settings_back
import kmpbackbone.composeapp.generated.resources.settings_coach_title
import kmpbackbone.composeapp.generated.resources.settings_error_generic
import kmpbackbone.composeapp.generated.resources.settings_error_not_found
import kmpbackbone.composeapp.generated.resources.settings_error_storage
import kmpbackbone.composeapp.generated.resources.settings_error_validation
import kmpbackbone.composeapp.generated.resources.settings_loading
import kmpbackbone.composeapp.generated.resources.settings_notifications_placeholder
import kmpbackbone.composeapp.generated.resources.settings_notifications_title
import kmpbackbone.composeapp.generated.resources.settings_plan_active_days_label
import kmpbackbone.composeapp.generated.resources.settings_plan_bedtime_label
import kmpbackbone.composeapp.generated.resources.settings_plan_title
import kmpbackbone.composeapp.generated.resources.settings_plan_tolerance_label
import kmpbackbone.composeapp.generated.resources.settings_plan_wake_label
import kmpbackbone.composeapp.generated.resources.settings_premium_body
import kmpbackbone.composeapp.generated.resources.settings_premium_title
import kmpbackbone.composeapp.generated.resources.settings_retry
import kmpbackbone.composeapp.generated.resources.settings_save
import kmpbackbone.composeapp.generated.resources.settings_title
import kmpbackbone.composeapp.generated.resources.symbol_minus
import kmpbackbone.composeapp.generated.resources.symbol_plus
import kmpbackbone.composeapp.generated.resources.time_format_hh_mm
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onBedtimeChanged: (LocalTime) -> Unit,
    onWakeTimeChanged: (LocalTime) -> Unit,
    onActiveDayToggled: (DayOfWeek) -> Unit,
    onCoachStyleSelected: (CoachStyle) -> Unit,
    onSave: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error, onRefresh)
            uiState.plan != null -> SettingsContent(
                uiState = uiState,
                onBack = onBack,
                onBedtimeChanged = onBedtimeChanged,
                onWakeTimeChanged = onWakeTimeChanged,
                onActiveDayToggled = onActiveDayToggled,
                onCoachStyleSelected = onCoachStyleSelected,
                onSave = onSave,
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(Res.string.settings_loading),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ErrorState(error: DomainError, onRefresh: () -> Unit) {
    val message = when (error) {
        DomainError.NotFound -> Res.string.settings_error_not_found
        DomainError.Validation -> Res.string.settings_error_validation
        DomainError.Storage -> Res.string.settings_error_storage
        DomainError.Conflict -> Res.string.settings_error_generic
        DomainError.Unknown -> Res.string.settings_error_generic
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRefresh,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text(text = stringResource(Res.string.settings_retry))
        }
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onBedtimeChanged: (LocalTime) -> Unit,
    onWakeTimeChanged: (LocalTime) -> Unit,
    onActiveDayToggled: (DayOfWeek) -> Unit,
    onCoachStyleSelected: (CoachStyle) -> Unit,
    onSave: () -> Unit,
) {
    val plan = uiState.plan ?: return
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onBack) {
                Text(text = stringResource(Res.string.settings_back))
            }
            Text(
                text = stringResource(Res.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.width(72.dp))
        }
        SettingsSectionTitle(text = stringResource(Res.string.settings_plan_title))
        TimeAdjuster(
            label = stringResource(Res.string.settings_plan_bedtime_label),
            time = plan.planStartLocalTime,
            onMinus = { onBedtimeChanged(adjustTime(plan.planStartLocalTime, -15)) },
            onPlus = { onBedtimeChanged(adjustTime(plan.planStartLocalTime, 15)) },
        )
        TimeAdjuster(
            label = stringResource(Res.string.settings_plan_wake_label),
            time = plan.planEndLocalTime,
            onMinus = { onWakeTimeChanged(adjustTime(plan.planEndLocalTime, -15)) },
            onPlus = { onWakeTimeChanged(adjustTime(plan.planEndLocalTime, 15)) },
        )
        Text(
            text = stringResource(Res.string.settings_plan_active_days_label),
            style = MaterialTheme.typography.titleLarge,
        )
        val activeDays = activeDaysFromMask(plan.activeDaysMask)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(DayOfWeek.values().size) { index ->
                val day = DayOfWeek.values()[index]
                FilterChip(
                    selected = activeDays.contains(day),
                    onClick = { onActiveDayToggled(day) },
                    label = { Text(text = stringResource(dayLabelRes(day))) },
                )
            }
        }
        Text(
            text = stringResource(Res.string.settings_plan_tolerance_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        SettingsSectionTitle(text = stringResource(Res.string.settings_coach_title))
        CoachStyle.values().forEach { style ->
            SelectableCard(
                title = stringResource(coachTitleRes(style)),
                subtitle = stringResource(coachSampleRes(style)),
                selected = style == uiState.coachStyle,
                onClick = { onCoachStyleSelected(style) },
            )
        }
        SettingsSectionTitle(text = stringResource(Res.string.settings_notifications_title))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.settings_notifications_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(Res.string.settings_notifications_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }
                Switch(
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = null,
                    enabled = false,
                )
            }
        }
        SettingsSectionTitle(text = stringResource(Res.string.settings_premium_title))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.settings_premium_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(Res.string.settings_premium_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                LinearProgressIndicator(
                    progress = 0.35f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text(text = stringResource(Res.string.settings_save))
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun SelectableCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = if (selected) 4.dp else 1.dp,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun TimeAdjuster(
    label: String,
    time: LocalTime,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onMinus) {
                Text(text = stringResource(Res.string.symbol_minus))
            }
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.headlineMedium,
            )
            OutlinedButton(onClick = onPlus) {
                Text(text = stringResource(Res.string.symbol_plus))
            }
        }
    }
}

@Composable
private fun formatTime(time: LocalTime): String {
    return stringResource(Res.string.time_format_hh_mm, time.hour, time.minute)
}

private fun adjustTime(time: LocalTime, minutesDelta: Int): LocalTime {
    val totalMinutes = (time.hour * 60 + time.minute + minutesDelta) % (24 * 60)
    val normalized = if (totalMinutes < 0) totalMinutes + 24 * 60 else totalMinutes
    val hour = normalized / 60
    val minute = normalized % 60
    return LocalTime(hour = hour, minute = minute)
}

private fun dayLabelRes(day: DayOfWeek): StringResource {
    return when (day) {
        DayOfWeek.MONDAY -> Res.string.day_mon_short
        DayOfWeek.TUESDAY -> Res.string.day_tue_short
        DayOfWeek.WEDNESDAY -> Res.string.day_wed_short
        DayOfWeek.THURSDAY -> Res.string.day_thu_short
        DayOfWeek.FRIDAY -> Res.string.day_fri_short
        DayOfWeek.SATURDAY -> Res.string.day_sat_short
        DayOfWeek.SUNDAY -> Res.string.day_sun_short
    }
}

private fun coachTitleRes(style: CoachStyle): StringResource {
    return when (style) {
        CoachStyle.CHILL -> Res.string.coach_chill_title
        CoachStyle.HYPE -> Res.string.coach_hype_title
        CoachStyle.STRICT -> Res.string.coach_strict_title
    }
}

private fun coachSampleRes(style: CoachStyle): StringResource {
    return when (style) {
        CoachStyle.CHILL -> Res.string.coach_chill_sample
        CoachStyle.HYPE -> Res.string.coach_hype_sample
        CoachStyle.STRICT -> Res.string.coach_strict_sample
    }
}

private fun activeDaysFromMask(mask: Int): Set<DayOfWeek> {
    return DayOfWeek.entries
        .filter { day -> mask and (1 shl day.ordinal) != 0 }
        .toSet()
}
