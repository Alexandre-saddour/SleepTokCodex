package com.example.kmpbackbone.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.CoachStyle
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonCard
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.theme.NeonColors
import com.example.kmpbackbone.viewmodel.OnboardingGoal
import com.example.kmpbackbone.viewmodel.OnboardingStep
import com.example.kmpbackbone.viewmodel.OnboardingUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.app_name
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
import kmpbackbone.composeapp.generated.resources.goal_better_sleep
import kmpbackbone.composeapp.generated.resources.goal_early_wake
import kmpbackbone.composeapp.generated.resources.goal_productivity
import kmpbackbone.composeapp.generated.resources.goal_routine
import kmpbackbone.composeapp.generated.resources.list_separator
import kmpbackbone.composeapp.generated.resources.onboarding_back
import kmpbackbone.composeapp.generated.resources.onboarding_coach_placeholder
import kmpbackbone.composeapp.generated.resources.onboarding_coach_title
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_slide1_body
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_slide1_title
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_slide2_body
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_slide2_title
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_slide3_body
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_slide3_title
import kmpbackbone.composeapp.generated.resources.onboarding_gamification_title
import kmpbackbone.composeapp.generated.resources.onboarding_goal_placeholder
import kmpbackbone.composeapp.generated.resources.onboarding_goal_title
import kmpbackbone.composeapp.generated.resources.onboarding_next
import kmpbackbone.composeapp.generated.resources.onboarding_plan_active_days_label
import kmpbackbone.composeapp.generated.resources.onboarding_plan_bedtime_label
import kmpbackbone.composeapp.generated.resources.onboarding_plan_summary_value
import kmpbackbone.composeapp.generated.resources.onboarding_plan_title
import kmpbackbone.composeapp.generated.resources.onboarding_plan_tolerance_label
import kmpbackbone.composeapp.generated.resources.onboarding_plan_wake_label
import kmpbackbone.composeapp.generated.resources.onboarding_ready_coach_label
import kmpbackbone.composeapp.generated.resources.onboarding_ready_cta
import kmpbackbone.composeapp.generated.resources.onboarding_ready_days_label
import kmpbackbone.composeapp.generated.resources.onboarding_ready_goal_label
import kmpbackbone.composeapp.generated.resources.onboarding_ready_plan_label
import kmpbackbone.composeapp.generated.resources.onboarding_ready_summary_title
import kmpbackbone.composeapp.generated.resources.onboarding_ready_title
import kmpbackbone.composeapp.generated.resources.onboarding_step_indicator
import kmpbackbone.composeapp.generated.resources.onboarding_welcome_cta
import kmpbackbone.composeapp.generated.resources.onboarding_welcome_tagline
import kmpbackbone.composeapp.generated.resources.onboarding_welcome_title
import kmpbackbone.composeapp.generated.resources.symbol_minus
import kmpbackbone.composeapp.generated.resources.symbol_plus
import kmpbackbone.composeapp.generated.resources.time_format_hh_mm
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onGoalSelected: (OnboardingGoal) -> Unit,
    onCoachStyleSelected: (CoachStyle) -> Unit,
    onBedtimeChanged: (LocalTime) -> Unit,
    onWakeTimeChanged: (LocalTime) -> Unit,
    onActiveDayToggled: (DayOfWeek) -> Unit,
) {
    val step = remember(uiState.stepIndex) {
        OnboardingStep.entries.getOrElse(uiState.stepIndex) { OnboardingStep.WELCOME }
    }
    val stepCount = OnboardingStep.entries.size

    NeonGradientBackground(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = NeonColors.NeonElectricBlue,
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                StepHeader(stepIndex = step.ordinal + 1, stepCount = stepCount)
                OnboardingStepContent(
                    step = step,
                    uiState = uiState,
                    onGoalSelected = onGoalSelected,
                    onCoachStyleSelected = onCoachStyleSelected,
                    onBedtimeChanged = onBedtimeChanged,
                    onWakeTimeChanged = onWakeTimeChanged,
                    onActiveDayToggled = onActiveDayToggled,
                )
                Spacer(modifier = Modifier.weight(1f))
                OnboardingActions(
                    step = step,
                    uiState = uiState,
                    onNext = onNext,
                    onBack = onBack,
                    onComplete = onComplete,
                )
            }
        }
    }
}

@Composable
private fun StepHeader(stepIndex: Int, stepCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.NeonElectricBlue,
        )
        Text(
            text = stringResource(Res.string.onboarding_step_indicator, stepIndex, stepCount),
            style = MaterialTheme.typography.labelLarge,
            color = NeonColors.TextSecondary,
        )
    }
}

@Composable
private fun OnboardingStepContent(
    step: OnboardingStep,
    uiState: OnboardingUiState,
    onGoalSelected: (OnboardingGoal) -> Unit,
    onCoachStyleSelected: (CoachStyle) -> Unit,
    onBedtimeChanged: (LocalTime) -> Unit,
    onWakeTimeChanged: (LocalTime) -> Unit,
    onActiveDayToggled: (DayOfWeek) -> Unit,
) {
    when (step) {
        OnboardingStep.WELCOME -> WelcomeStep()
        OnboardingStep.GOAL -> GoalStep(
            selectedGoal = uiState.selectedGoal,
            onGoalSelected = onGoalSelected,
        )
        OnboardingStep.COACH_STYLE -> CoachStep(
            selectedStyle = uiState.coachStyle,
            onCoachStyleSelected = onCoachStyleSelected,
        )
        OnboardingStep.PLAN -> PlanStep(
            bedtime = uiState.bedtime,
            wakeTime = uiState.wakeTime,
            activeDays = uiState.activeDays,
            onBedtimeChanged = onBedtimeChanged,
            onWakeTimeChanged = onWakeTimeChanged,
            onActiveDayToggled = onActiveDayToggled,
        )
        OnboardingStep.GAMIFICATION -> GamificationStep()
        OnboardingStep.READY -> ReadyStep(uiState = uiState)
    }
}

@Composable
private fun WelcomeStep() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.onboarding_welcome_title),
            style = MaterialTheme.typography.displayLarge,
            color = NeonColors.TextPrimary,
        )
        Text(
            text = stringResource(Res.string.onboarding_welcome_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
        )
    }
}

@Composable
private fun GoalStep(
    selectedGoal: OnboardingGoal?,
    onGoalSelected: (OnboardingGoal) -> Unit,
) {
    val goals = listOf(
        OnboardingGoal.BETTER_SLEEP,
        OnboardingGoal.ROUTINE,
        OnboardingGoal.PRODUCTIVITY,
        OnboardingGoal.EARLY_WAKE,
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.onboarding_goal_title),
            style = MaterialTheme.typography.headlineMedium,
            color = NeonColors.TextPrimary,
        )
        goals.chunked(2).forEach { rowGoals ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowGoals.forEach { goal ->
                    NeonSelectableCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(goalTitleRes(goal)),
                        selected = goal == selectedGoal,
                        onClick = { onGoalSelected(goal) },
                        accentColor = NeonColors.NeonElectricBlue,
                    )
                }
            }
        }
    }
}

@Composable
private fun CoachStep(
    selectedStyle: CoachStyle?,
    onCoachStyleSelected: (CoachStyle) -> Unit,
) {
    val styles = listOf(CoachStyle.CHILL, CoachStyle.HYPE, CoachStyle.STRICT)
    val styleColors = mapOf(
        CoachStyle.CHILL to NeonColors.NeonCyan,
        CoachStyle.HYPE to NeonColors.NeonPink,
        CoachStyle.STRICT to NeonColors.NeonOrange,
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.onboarding_coach_title),
            style = MaterialTheme.typography.headlineMedium,
            color = NeonColors.TextPrimary,
        )
        styles.forEach { style ->
            NeonSelectableCard(
                title = stringResource(coachTitleRes(style)),
                subtitle = stringResource(coachSampleRes(style)),
                selected = style == selectedStyle,
                onClick = { onCoachStyleSelected(style) },
                accentColor = styleColors[style] ?: NeonColors.NeonElectricBlue,
            )
        }
    }
}

@Composable
private fun PlanStep(
    bedtime: LocalTime,
    wakeTime: LocalTime,
    activeDays: Set<DayOfWeek>,
    onBedtimeChanged: (LocalTime) -> Unit,
    onWakeTimeChanged: (LocalTime) -> Unit,
    onActiveDayToggled: (DayOfWeek) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(
            text = stringResource(Res.string.onboarding_plan_title),
            style = MaterialTheme.typography.headlineMedium,
            color = NeonColors.TextPrimary,
        )
        NeonTimeAdjuster(
            label = stringResource(Res.string.onboarding_plan_bedtime_label),
            time = bedtime,
            onMinus = { onBedtimeChanged(adjustTime(bedtime, -15)) },
            onPlus = { onBedtimeChanged(adjustTime(bedtime, 15)) },
        )
        NeonTimeAdjuster(
            label = stringResource(Res.string.onboarding_plan_wake_label),
            time = wakeTime,
            onMinus = { onWakeTimeChanged(adjustTime(wakeTime, -15)) },
            onPlus = { onWakeTimeChanged(adjustTime(wakeTime, 15)) },
        )
        Text(
            text = stringResource(Res.string.onboarding_plan_active_days_label),
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )
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
            text = stringResource(Res.string.onboarding_plan_tolerance_label),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextMuted,
        )
    }
}

@Composable
private fun GamificationStep() {
    val slides = listOf(
        Slide(
            title = stringResource(Res.string.onboarding_gamification_slide1_title),
            body = stringResource(Res.string.onboarding_gamification_slide1_body),
            color = NeonColors.NeonGreen,
        ),
        Slide(
            title = stringResource(Res.string.onboarding_gamification_slide2_title),
            body = stringResource(Res.string.onboarding_gamification_slide2_body),
            color = NeonColors.NeonYellow,
        ),
        Slide(
            title = stringResource(Res.string.onboarding_gamification_slide3_title),
            body = stringResource(Res.string.onboarding_gamification_slide3_body),
            color = NeonColors.NeonPurple,
        ),
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.onboarding_gamification_title),
            style = MaterialTheme.typography.headlineMedium,
            color = NeonColors.TextPrimary,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(slides) { slide ->
                NeonCard(
                    modifier = Modifier
                        .width(240.dp)
                        .aspectRatio(1.1f),
                    glowColor = slide.color,
                    glowIntensity = 0.3f,
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = slide.color,
                        )
                        Text(
                            text = slide.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeonColors.TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadyStep(uiState: OnboardingUiState) {
    val goalText = uiState.selectedGoal?.let { stringResource(goalTitleRes(it)) }
        ?: stringResource(Res.string.onboarding_goal_placeholder)
    val coachText = uiState.coachStyle?.let { stringResource(coachTitleRes(it)) }
        ?: stringResource(Res.string.onboarding_coach_placeholder)
    val timeSeparator = stringResource(Res.string.list_separator)
    val dayLabels = uiState.activeDays
        .sortedBy { it.ordinal }
        .map { stringResource(dayLabelRes(it)) }
    val daySummary = dayLabels.joinToString(timeSeparator)
    val bedtimeText = formatTime(uiState.bedtime)
    val wakeText = formatTime(uiState.wakeTime)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.onboarding_ready_title),
            style = MaterialTheme.typography.displayLarge,
            color = NeonColors.NeonGreen,
        )
        Text(
            text = stringResource(Res.string.onboarding_ready_summary_title),
            style = MaterialTheme.typography.headlineMedium,
            color = NeonColors.TextPrimary,
        )
        NeonCard(
            glowColor = NeonColors.NeonGreen,
            glowIntensity = 0.25f,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryRow(
                    label = stringResource(Res.string.onboarding_ready_goal_label),
                    value = goalText,
                )
                SummaryRow(
                    label = stringResource(Res.string.onboarding_ready_coach_label),
                    value = coachText,
                )
                SummaryRow(
                    label = stringResource(Res.string.onboarding_ready_plan_label),
                    value = stringResource(
                        Res.string.onboarding_plan_summary_value,
                        bedtimeText,
                        wakeText,
                    ),
                )
                SummaryRow(
                    label = stringResource(Res.string.onboarding_ready_days_label),
                    value = daySummary,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = NeonColors.TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun OnboardingActions(
    step: OnboardingStep,
    uiState: OnboardingUiState,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val canContinue = when (step) {
        OnboardingStep.GOAL -> uiState.selectedGoal != null
        OnboardingStep.COACH_STYLE -> uiState.coachStyle != null
        OnboardingStep.PLAN -> uiState.activeDays.isNotEmpty()
        else -> true
    }
    val primaryLabel = when (step) {
        OnboardingStep.WELCOME -> Res.string.onboarding_welcome_cta
        OnboardingStep.READY -> Res.string.onboarding_ready_cta
        else -> Res.string.onboarding_next
    }
    val onPrimary = if (step == OnboardingStep.READY) onComplete else onNext
    val buttonColor = when (step) {
        OnboardingStep.READY -> NeonColors.NeonGreen
        else -> NeonColors.NeonElectricBlue
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (step != OnboardingStep.WELCOME) {
            TextButton(onClick = onBack) {
                Text(
                    text = stringResource(Res.string.onboarding_back),
                    color = NeonColors.TextSecondary,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        NeonButton(
            onClick = onPrimary,
            enabled = canContinue,
            glowColor = buttonColor,
            modifier = Modifier.weight(2f),
        ) {
            Text(
                text = stringResource(primaryLabel),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun NeonSelectableCard(
    title: String,
    subtitle: String? = null,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
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
        modifier = modifier
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp),
            ),
        shape = RoundedCornerShape(20.dp),
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
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonColors.TextSecondary,
                )
            }
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

private fun goalTitleRes(goal: OnboardingGoal): StringResource {
    return when (goal) {
        OnboardingGoal.BETTER_SLEEP -> Res.string.goal_better_sleep
        OnboardingGoal.ROUTINE -> Res.string.goal_routine
        OnboardingGoal.PRODUCTIVITY -> Res.string.goal_productivity
        OnboardingGoal.EARLY_WAKE -> Res.string.goal_early_wake
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

private data class Slide(
    val title: String,
    val body: String,
    val color: androidx.compose.ui.graphics.Color,
)
