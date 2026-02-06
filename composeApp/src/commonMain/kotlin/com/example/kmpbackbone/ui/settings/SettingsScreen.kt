package com.example.kmpbackbone.ui.settings

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.CoachStyle
import com.example.domain.result.DomainError
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonCard
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.components.NeonProgressBar
import com.example.kmpbackbone.ui.components.NeonSectionHeader
import com.example.kmpbackbone.ui.theme.NeonColors
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
    NeonGradientBackground(
        modifier = Modifier.padding(20.dp),
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
        CircularProgressIndicator(
            color = NeonColors.NeonElectricBlue,
        )
        Text(
            text = stringResource(Res.string.settings_loading),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
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
            color = NeonColors.StatusFail,
            textAlign = TextAlign.Center,
        )
        NeonButton(
            onClick = onRefresh,
            modifier = Modifier.padding(top = 12.dp),
            glowColor = NeonColors.NeonElectricBlue,
        ) {
            Text(
                text = stringResource(Res.string.settings_retry),
                style = MaterialTheme.typography.labelLarge,
            )
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                color = NeonColors.NeonElectricBlue.copy(alpha = 0.1f),
                contentColor = NeonColors.NeonElectricBlue,
            ) {
                Text(
                    text = stringResource(Res.string.settings_back),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
            Text(
                text = stringResource(Res.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonColors.TextPrimary,
            )
            Spacer(modifier = Modifier.width(72.dp))
        }

        NeonSectionHeader(
            text = stringResource(Res.string.settings_plan_title),
            accentColor = NeonColors.NeonElectricBlue,
        )

        NeonTimeAdjuster(
            label = stringResource(Res.string.settings_plan_bedtime_label),
            time = plan.planStartLocalTime,
            onMinus = { onBedtimeChanged(adjustTime(plan.planStartLocalTime, -15)) },
            onPlus = { onBedtimeChanged(adjustTime(plan.planStartLocalTime, 15)) },
        )

        NeonTimeAdjuster(
            label = stringResource(Res.string.settings_plan_wake_label),
            time = plan.planEndLocalTime,
            onMinus = { onWakeTimeChanged(adjustTime(plan.planEndLocalTime, -15)) },
            onPlus = { onWakeTimeChanged(adjustTime(plan.planEndLocalTime, 15)) },
        )

        Text(
            text = stringResource(Res.string.settings_plan_active_days_label),
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )

        val activeDays = plan.activeDays
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(DayOfWeek.entries) { day ->
                FilterChip(
                    selected = activeDays.contains(day),
                    onClick = { onActiveDayToggled(day) },
                    label = { Text(text = stringResource(dayLabelRes(day))) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonColors.NeonElectricBlue.copy(alpha = 0.2f),
                        selectedLabelColor = NeonColors.NeonElectricBlue,
                        containerColor = NeonColors.NeonDarkSurfaceVariant,
                        labelColor = NeonColors.TextSecondary,
                    ),
                )
            }
        }

        Text(
            text = stringResource(
                Res.string.settings_plan_tolerance_label,
                plan.toleranceStartMinutes,
                plan.toleranceEndMinutes,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextMuted,
        )

        NeonSectionHeader(
            text = stringResource(Res.string.settings_coach_title),
            accentColor = NeonColors.NeonPink,
        )

        val coachColors = mapOf(
            CoachStyle.CHILL to NeonColors.NeonCyan,
            CoachStyle.HYPE to NeonColors.NeonPink,
            CoachStyle.STRICT to NeonColors.NeonOrange,
        )

        CoachStyle.entries.forEach { style ->
            NeonSelectableCard(
                title = stringResource(coachTitleRes(style)),
                subtitle = stringResource(coachSampleRes(style)),
                selected = style == uiState.coachStyle,
                onClick = { onCoachStyleSelected(style) },
                accentColor = coachColors[style] ?: NeonColors.NeonElectricBlue,
            )
        }

        NeonSectionHeader(
            text = stringResource(Res.string.settings_notifications_title),
            accentColor = NeonColors.NeonPurple,
        )

        NeonCard(
            glowColor = NeonColors.NeonPurple,
            glowIntensity = 0.15f,
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
                        color = NeonColors.TextPrimary,
                    )
                    Text(
                        text = stringResource(Res.string.settings_notifications_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonColors.TextMuted,
                    )
                }
                Switch(
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = null,
                    enabled = false,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonColors.NeonPurple,
                        checkedTrackColor = NeonColors.NeonPurple.copy(alpha = 0.3f),
                        uncheckedThumbColor = NeonColors.TextMuted,
                        uncheckedTrackColor = NeonColors.NeonDarkSurfaceVariant,
                    ),
                )
            }
        }

        NeonSectionHeader(
            text = stringResource(Res.string.settings_premium_title),
            accentColor = NeonColors.NeonYellow,
        )

        NeonCard(
            glowColor = NeonColors.NeonYellow,
            glowIntensity = 0.2f,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.settings_premium_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonColors.NeonYellow,
                )
                Text(
                    text = stringResource(Res.string.settings_premium_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonColors.TextSecondary,
                )
                NeonProgressBar(
                    progress = 0.35f,
                    progressColor = NeonColors.NeonYellow,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        NeonButton(
            onClick = onSave,
            enabled = !uiState.isSaving,
            glowColor = NeonColors.NeonGreen,
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                    color = NeonColors.NeonDarkBackground,
                )
            }
            Text(
                text = stringResource(Res.string.settings_save),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun NeonSelectableCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: androidx.compose.ui.graphics.Color,
) {
    val borderColor = when {
        selected -> accentColor
        else -> NeonColors.Outline
    }
    val backgroundColor = when {
        selected -> accentColor.copy(alpha = 0.1f)
        else -> NeonColors.NeonDarkSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.large,
            ),
        shape = MaterialTheme.shapes.large,
        color = backgroundColor,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = if (selected) accentColor else NeonColors.TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = NeonColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun NeonTimeAdjuster(
    label: String,
    time: LocalTime,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeonTimeButton(
                text = stringResource(Res.string.symbol_minus),
                onClick = onMinus,
            )
            Text(
                text = formatTime(time),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonColors.NeonElectricBlue,
            )
            NeonTimeButton(
                text = stringResource(Res.string.symbol_plus),
                onClick = onPlus,
            )
        }
    }
}

@Composable
private fun NeonTimeButton(
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
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
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
