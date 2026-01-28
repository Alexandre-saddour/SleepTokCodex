package com.example.kmpbackbone.ui.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.Night
import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.result.DomainError
import com.example.domain.scoring.LevelCalculator
import com.example.kmpbackbone.viewmodel.HomeMode
import com.example.kmpbackbone.viewmodel.HomeUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.daily_chest_available_cta
import kmpbackbone.composeapp.generated.resources.daily_chest_available_title
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
import kmpbackbone.composeapp.generated.resources.duration_hours
import kmpbackbone.composeapp.generated.resources.duration_hours_minutes
import kmpbackbone.composeapp.generated.resources.duration_minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
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
    val background = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        when {
            uiState.isLoading -> {
                LoadingState()
            }
            uiState.user == null || uiState.plan == null -> {
                EmptyState()
            }
            else -> {
                Column(
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
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
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
        color = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
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
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
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
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                ) {
                    Text(
                        text = stringResource(Res.string.home_streak_label, user.streakCurrent),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Text(
                    text = stringResource(Res.string.home_level_label, levelProgress.level),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            LinearProgressIndicator(
                progress = { xpRatio.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = stringResource(
                    Res.string.home_xp_progress_label,
                    levelProgress.xpTotal,
                    levelProgress.nextLevelXp,
                ),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }

    Button(
        onClick = onPlay,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Text(text = stringResource(Res.string.home_primary_play))
    }

    OutlinedButton(
        onClick = onEditPlan,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Text(text = stringResource(Res.string.home_secondary_edit_plan))
    }

    when {
        canClaimDailyChest -> Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            shape = RoundedCornerShape(20.dp),
            onClick = onOpenDailyChest,
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
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.daily_chest_available_cta),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
        else -> Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp),
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
                )
            }
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
            )
            Text(
                text = stringResource(Res.string.home_night_mode_hint),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BreathingOrb()
            Text(
                text = elapsedText,
                style = MaterialTheme.typography.displayLarge,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HoldToStopButton(
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
                            .width(24.dp)
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
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
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
                )
                Button(
                    onClick = onClaimResult,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(text = stringResource(Res.string.home_claim_cta))
                }
            }
        }
    }
}

@Composable
private fun BreathingOrb() {
    val transition = rememberInfiniteTransition(label = "breathing-orb")
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathing-scale",
    )
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathing-alpha",
    )
    val glow = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.tertiary.copy(alpha = alpha),
            Color.Transparent,
        ),
    )
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(180.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
            )
            .background(glow, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(84.dp)
                .height(84.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape,
                ),
        )
    }
}

@Composable
private fun HoldToStopButton(
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
                .height(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
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
                color = MaterialTheme.colorScheme.onTertiary,
            )
        }
        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
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
