package com.example.kmpbackbone.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.Night
import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.result.DomainError
import com.example.domain.scoring.LevelCalculator
import com.example.kmpbackbone.ui.components.NeonBadge
import com.example.kmpbackbone.ui.components.NeonBreathingOrb
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonCard
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.components.NeonProgressBar
import com.example.kmpbackbone.ui.theme.NeonColors
import com.example.kmpbackbone.viewmodel.HomeMode
import com.example.kmpbackbone.viewmodel.HomeUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.daily_chest_available_cta
import kmpbackbone.composeapp.generated.resources.daily_chest_available_title
import kmpbackbone.composeapp.generated.resources.duration_hours
import kmpbackbone.composeapp.generated.resources.duration_hours_minutes
import kmpbackbone.composeapp.generated.resources.duration_minutes
import kmpbackbone.composeapp.generated.resources.home_claim_cta
import kmpbackbone.composeapp.generated.resources.home_empty_state
import kmpbackbone.composeapp.generated.resources.home_error_conflict
import kmpbackbone.composeapp.generated.resources.home_error_generic
import kmpbackbone.composeapp.generated.resources.home_error_not_found
import kmpbackbone.composeapp.generated.resources.home_error_storage
import kmpbackbone.composeapp.generated.resources.home_error_validation
import kmpbackbone.composeapp.generated.resources.home_hold_to_stop
import kmpbackbone.composeapp.generated.resources.home_level_label
import kmpbackbone.composeapp.generated.resources.home_night_mode_hint
import kmpbackbone.composeapp.generated.resources.home_night_mode_title
import kmpbackbone.composeapp.generated.resources.home_plan_time
import kmpbackbone.composeapp.generated.resources.home_primary_play
import kmpbackbone.composeapp.generated.resources.home_result_ready
import kmpbackbone.composeapp.generated.resources.home_secondary_edit_plan
import kmpbackbone.composeapp.generated.resources.home_streak_label
import kmpbackbone.composeapp.generated.resources.home_teaser_next_reward
import kmpbackbone.composeapp.generated.resources.home_timer_format
import kmpbackbone.composeapp.generated.resources.home_title_tonight_quest
import kmpbackbone.composeapp.generated.resources.home_xp_progress_label
import kmpbackbone.composeapp.generated.resources.time_format_hh_mm
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onClaimResult: () -> Unit,
    onEditPlan: () -> Unit,
    onOpenDailyChest: () -> Unit,
) {
    NeonGradientBackground(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.user == null || uiState.plan == null -> EmptyState()
            else -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                if (uiState.error != null) {
                    ErrorBanner(uiState.error)
                }
                when (uiState.mode) {
                    HomeMode.BeforeNight -> BeforeNightContent(
                        user = uiState.user,
                        plan = uiState.plan,
                        nextRewardWins = uiState.nextRewardWins,
                        canClaimDailyChest = uiState.canClaimDailyChest,
                        onPlay = onPlay,
                        onEditPlan = onEditPlan,
                        onOpenDailyChest = onOpenDailyChest,
                    )
                    HomeMode.NightMode -> NightModeContent(
                        night = uiState.activeNight,
                        onStop = onStop,
                        isActionInProgress = uiState.isActionInProgress,
                    )
                    HomeMode.PostStopClaim -> PostStopContent(
                        onClaimResult = onClaimResult,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = NeonColors.NeonElectricBlue,
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.home_empty_state),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = NeonColors.TextSecondary,
        )
    }
}

@Composable
private fun ErrorBanner(error: DomainError) {
    val message = when (error) {
        DomainError.NotFound -> Res.string.home_error_not_found
        DomainError.Validation -> Res.string.home_error_validation
        DomainError.Storage -> Res.string.home_error_storage
        DomainError.Conflict -> Res.string.home_error_conflict
        DomainError.Unknown -> Res.string.home_error_generic
    }
    Surface(
        color = NeonColors.StatusFail.copy(alpha = 0.2f),
        contentColor = NeonColors.StatusFail,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = stringResource(message),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun BeforeNightContent(
    user: User,
    plan: SleepPlan,
    nextRewardWins: Int,
    canClaimDailyChest: Boolean,
    onPlay: () -> Unit,
    onEditPlan: () -> Unit,
    onOpenDailyChest: () -> Unit,
) {
    val startText = formatTime(plan.planStartLocalTime.hour, plan.planStartLocalTime.minute)
    val endText = formatTime(plan.planEndLocalTime.hour, plan.planEndLocalTime.minute)
    val durationText = formatDuration(planDurationMinutes(plan))
    val planText = stringResource(Res.string.home_plan_time, startText, endText, durationText)

    val levelProgress = remember(user.xpTotal) { LevelCalculator.levelProgress(user.xpTotal) }
    val xpRatio = remember(levelProgress) {
        levelProgress.xpInLevel.toFloat() / levelProgress.levelSpan.toFloat()
    }

    Text(
        text = stringResource(Res.string.home_title_tonight_quest),
        style = MaterialTheme.typography.headlineMedium,
        color = NeonColors.TextPrimary,
    )

    NeonCard(
        glowColor = NeonColors.NeonElectricBlue,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = planText,
                style = MaterialTheme.typography.titleLarge,
                color = NeonColors.TextPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NeonBadge(
                    text = stringResource(Res.string.home_streak_label, user.streakCurrent),
                    color = NeonColors.NeonGreen,
                )
                Text(
                    text = stringResource(Res.string.home_level_label, levelProgress.level),
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonColors.NeonYellow,
                )
            }
            NeonProgressBar(
                progress = xpRatio.coerceIn(0f, 1f),
                progressColor = NeonColors.NeonYellow,
            )
            Text(
                text = stringResource(
                    Res.string.home_xp_progress_label,
                    levelProgress.xpTotal,
                    levelProgress.nextLevelXp,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = NeonColors.TextSecondary,
            )
        }
    }

    NeonButton(
        onClick = onPlay,
        glowColor = NeonColors.NeonElectricBlue,
    ) {
        Text(
            text = stringResource(Res.string.home_primary_play),
            style = MaterialTheme.typography.labelLarge,
        )
    }

    NeonSecondaryButton(
        onClick = onEditPlan,
    ) {
        Text(
            text = stringResource(Res.string.home_secondary_edit_plan),
            style = MaterialTheme.typography.labelLarge,
        )
    }

    when {
        canClaimDailyChest -> NeonCard(
            glowColor = NeonColors.NeonYellow,
        ) {
            Surface(
                onClick = onOpenDailyChest,
                color = Color.Transparent,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.daily_chest_available_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonColors.NeonYellow,
                    )
                    NeonBadge(
                        text = stringResource(Res.string.daily_chest_available_cta),
                        color = NeonColors.NeonYellow,
                    )
                }
            }
        }
        else -> NeonCard(
            glowColor = NeonColors.NeonPurple,
            glowIntensity = 0.2f,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.home_teaser_next_reward, nextRewardWins),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun NeonSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        contentColor = NeonColors.NeonElectricBlue,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            NeonColors.NeonElectricBlue.copy(alpha = 0.1f),
                            NeonColors.NeonElectricBlue.copy(alpha = 0.05f),
                        ),
                    ),
                    shape = RoundedCornerShape(18.dp),
                )
                .padding(1.dp)
                .background(
                    color = NeonColors.NeonDarkSurface,
                    shape = RoundedCornerShape(17.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
private fun NightModeContent(
    night: Night?,
    onStop: () -> Unit,
    isActionInProgress: Boolean,
) {
    if (night == null) {
        EmptyState()
        return
    }
    val elapsedText = rememberElapsedText(night.startAt.toEpochMilliseconds())

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.home_night_mode_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonColors.TextPrimary,
            )
            Text(
                text = stringResource(Res.string.home_night_mode_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = NeonColors.TextSecondary,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NeonBreathingOrb(
                primaryColor = NeonColors.NeonCyan,
                secondaryColor = NeonColors.NeonGreen,
            )
            Text(
                text = elapsedText,
                style = MaterialTheme.typography.displayLarge,
                color = NeonColors.NeonCyan,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NeonHoldToStopButton(
                enabled = !isActionInProgress,
                onHoldComplete = onStop,
            )
            if (isActionInProgress) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        color = NeonColors.NeonElectricBlue,
                    )
                }
            }
        }
    }
}

@Composable
private fun PostStopContent(
    onClaimResult: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeonCard(
            glowColor = NeonColors.NeonGreen,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.home_result_ready),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = NeonColors.NeonGreen,
                )
                NeonButton(
                    onClick = onClaimResult,
                    glowColor = NeonColors.NeonGreen,
                ) {
                    Text(
                        text = stringResource(Res.string.home_claim_cta),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun NeonHoldToStopButton(
    enabled: Boolean,
    onHoldComplete: () -> Unit,
) {
    val holdDurationMs = 1200
    val progress = remember { Animatable(0f) }
    val currentOnHoldComplete by rememberUpdatedState(onHoldComplete)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
        ) {
            // Glow layer
            if (enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(4.dp)
                        .blur(16.dp)
                        .background(
                            NeonColors.NeonOrange.copy(alpha = 0.4f),
                            RoundedCornerShape(32.dp),
                        ),
                )
            }
            // Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                NeonColors.NeonOrange,
                                NeonColors.NeonYellow,
                            ),
                        ),
                        shape = RoundedCornerShape(32.dp),
                    )
                    .pointerInput(enabled) {
                        detectTapGestures(
                            onPress = {
                                if (!enabled) {
                                    return@detectTapGestures
                                }
                                progress.snapTo(0f)
                                coroutineScope {
                                    val job = launch {
                                        progress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = holdDurationMs,
                                                easing = LinearEasing,
                                            ),
                                        )
                                        currentOnHoldComplete()
                                    }
                                    val released = tryAwaitRelease()
                                    if (released && !job.isCompleted) {
                                        job.cancel()
                                    }
                                }
                                progress.snapTo(0f)
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.home_hold_to_stop),
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonColors.NeonDarkBackground,
                )
            }
        }
        NeonProgressBar(
            progress = progress.value,
            progressColor = NeonColors.NeonOrange,
            height = 4.dp,
        )
    }
}

@Composable
private fun formatTime(hour: Int, minute: Int): String {
    return stringResource(Res.string.time_format_hh_mm, hour, minute)
}

@Composable
private fun formatDuration(totalMinutes: Int): String {
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

private fun planDurationMinutes(plan: SleepPlan): Int {
    val startMinutes = plan.planStartLocalTime.hour * 60 + plan.planStartLocalTime.minute
    val endMinutes = plan.planEndLocalTime.hour * 60 + plan.planEndLocalTime.minute
    return if (endMinutes >= startMinutes) {
        endMinutes - startMinutes
    } else {
        (24 * 60 - startMinutes) + endMinutes
    }
}

@Composable
private fun rememberElapsedText(startAtMillis: Long): String {
    var elapsedSeconds by remember(startAtMillis) { mutableStateOf(0L) }
    LaunchedEffect(startAtMillis) {
        while (true) {
            val now = Clock.System.now().toEpochMilliseconds()
            elapsedSeconds = ((now - startAtMillis) / 1000L).coerceAtLeast(0L)
            val delayMillis = 1000L - (now % 1000L)
            delay(delayMillis)
        }
    }
    val hours = (elapsedSeconds / 3600L).toInt()
    val minutes = ((elapsedSeconds % 3600L) / 60L).toInt()
    return stringResource(Res.string.home_timer_format, hours, minutes)
}
