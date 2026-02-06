package com.example.kmpbackbone.ui.nightresult

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.CoachStyle
import com.example.domain.model.NightResult
import com.example.domain.model.NightStatus
import com.example.domain.result.DomainError
import com.example.kmpbackbone.ui.components.AnimatedCounter
import com.example.kmpbackbone.ui.components.NeonBadge
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonCard
import com.example.kmpbackbone.ui.components.NeonConfetti
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.components.NeonScoreRing
import com.example.kmpbackbone.ui.theme.NeonColors
import com.example.kmpbackbone.viewmodel.NightResultUiEvent
import com.example.kmpbackbone.viewmodel.NightResultUiState
import com.example.kmpbackbone.viewmodel.NightResultViewModel
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.night_result_breakdown_title
import kmpbackbone.composeapp.generated.resources.night_result_continue_cta
import kmpbackbone.composeapp.generated.resources.night_result_error_generic
import kmpbackbone.composeapp.generated.resources.night_result_error_not_found
import kmpbackbone.composeapp.generated.resources.night_result_error_storage
import kmpbackbone.composeapp.generated.resources.night_result_error_validation
import kmpbackbone.composeapp.generated.resources.night_result_fail_chill
import kmpbackbone.composeapp.generated.resources.night_result_fail_hype
import kmpbackbone.composeapp.generated.resources.night_result_fail_strict
import kmpbackbone.composeapp.generated.resources.night_result_label_fail
import kmpbackbone.composeapp.generated.resources.night_result_label_in_progress
import kmpbackbone.composeapp.generated.resources.night_result_label_partial
import kmpbackbone.composeapp.generated.resources.night_result_label_success
import kmpbackbone.composeapp.generated.resources.night_result_loading
import kmpbackbone.composeapp.generated.resources.night_result_next_milestone
import kmpbackbone.composeapp.generated.resources.night_result_partial_chill
import kmpbackbone.composeapp.generated.resources.night_result_partial_hype
import kmpbackbone.composeapp.generated.resources.night_result_partial_strict
import kmpbackbone.composeapp.generated.resources.night_result_score_label
import kmpbackbone.composeapp.generated.resources.night_result_share
import kmpbackbone.composeapp.generated.resources.night_result_share_coming_soon
import kmpbackbone.composeapp.generated.resources.night_result_shield_available
import kmpbackbone.composeapp.generated.resources.night_result_shield_used
import kmpbackbone.composeapp.generated.resources.night_result_streak_label
import kmpbackbone.composeapp.generated.resources.night_result_success_chill
import kmpbackbone.composeapp.generated.resources.night_result_success_hype
import kmpbackbone.composeapp.generated.resources.night_result_success_strict
import kmpbackbone.composeapp.generated.resources.night_result_use_shield_cta
import kmpbackbone.composeapp.generated.resources.night_result_xp_base
import kmpbackbone.composeapp.generated.resources.night_result_xp_multiplier_value
import kmpbackbone.composeapp.generated.resources.night_result_xp_perfect
import kmpbackbone.composeapp.generated.resources.night_result_xp_score_bonus
import kmpbackbone.composeapp.generated.resources.night_result_xp_streak_multiplier
import kmpbackbone.composeapp.generated.resources.night_result_xp_talent_bonus
import kmpbackbone.composeapp.generated.resources.night_result_xp_talent_multiplier
import kmpbackbone.composeapp.generated.resources.night_result_xp_total
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NightResultRoot(
    nightId: Long,
    onBack: () -> Unit,
) {
    val viewModel: NightResultViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(nightId) {
        viewModel.load(nightId)
    }
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NightResultUiEvent.NavigateBack -> onBack()
            }
        }
    }

    NightResultScreen(
        uiState = uiState,
        onContinue = viewModel::onContinue,
        onUseShield = viewModel::onUseShield,
    )
}

@Composable
fun NightResultScreen(
    uiState: NightResultUiState,
    onContinue: () -> Unit,
    onUseShield: () -> Unit,
) {
    NeonGradientBackground(
        modifier = Modifier.padding(20.dp),
    ) {
        // Confetti overlay for success
        val showConfetti = uiState.result?.status == NightStatus.SUCCESS
        NeonConfetti(
            isActive = showConfetti,
            modifier = Modifier.fillMaxSize(),
        )

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error)
            uiState.result == null -> ErrorState(DomainError.Unknown)
            else -> ResultContent(
                uiState = uiState,
                onContinue = onContinue,
                onUseShield = onUseShield,
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
            text = stringResource(Res.string.night_result_loading),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ErrorState(error: DomainError) {
    val message = when (error) {
        DomainError.NotFound -> Res.string.night_result_error_not_found
        DomainError.Validation -> Res.string.night_result_error_validation
        DomainError.Storage -> Res.string.night_result_error_storage
        DomainError.Conflict -> Res.string.night_result_error_generic
        DomainError.Unknown -> Res.string.night_result_error_generic
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
    }
}

@Composable
private fun ResultContent(
    uiState: NightResultUiState,
    onContinue: () -> Unit,
    onUseShield: () -> Unit,
) {
    val result = uiState.result ?: return
    val statusLabel = statusLabel(result.status)
    val statusColor = statusColor(result.status)
    val coachMessage = coachMessage(result.status, uiState.coachStyle)
    val shieldPrompt = result.status == NightStatus.FAIL && uiState.shieldAvailable && !uiState.shieldUsed

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = statusLabel,
            style = MaterialTheme.typography.headlineMedium,
            color = statusColor,
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            NeonScoreRing(score = result.score)
        }

        Text(
            text = stringResource(Res.string.night_result_score_label, result.score),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
        )

        CoachMessageCard(message = coachMessage, statusColor = statusColor)

        StreakCard(
            streakBefore = result.streakBefore,
            streakAfter = result.streakAfter,
            shieldUsed = uiState.shieldUsed,
        )

        BreakdownCard(result = result)

        uiState.nextMilestone?.let { milestone ->
            MilestoneCard(milestone = milestone)
        }

        if (shieldPrompt) {
            ShieldCard(charges = uiState.shieldCharges)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.isApplying) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = NeonColors.NeonElectricBlue,
                )
            }
        } else {
            if (shieldPrompt) {
                NeonSecondaryButton(
                    onClick = onUseShield,
                    color = NeonColors.NeonCyan,
                ) {
                    Text(
                        text = stringResource(Res.string.night_result_use_shield_cta),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            NeonButton(
                onClick = onContinue,
                glowColor = statusColor,
            ) {
                Text(
                    text = stringResource(Res.string.night_result_continue_cta),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ShareButtonTeaser()
        }
    }
}

@Composable
private fun NeonSecondaryButton(
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
        ),
    ) {
        content()
    }
}

@Composable
private fun CoachMessageCard(message: String, statusColor: Color) {
    NeonCard(
        glowColor = statusColor,
        glowIntensity = 0.25f,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextPrimary,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun StreakCard(
    streakBefore: Int,
    streakAfter: Int,
    shieldUsed: Boolean,
) {
    NeonCard(
        glowColor = NeonColors.NeonGreen,
        glowIntensity = 0.2f,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.night_result_streak_label, streakBefore, streakAfter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonColors.TextPrimary,
                )
                NeonBadge(
                    text = streakAfter.toString(),
                    color = NeonColors.NeonGreen,
                )
            }
            if (shieldUsed) {
                Text(
                    text = stringResource(Res.string.night_result_shield_used),
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonColors.NeonCyan,
                )
            }
        }
    }
}

@Composable
private fun ShieldCard(charges: Int) {
    Surface(
        color = NeonColors.NeonCyan.copy(alpha = 0.15f),
        contentColor = NeonColors.NeonCyan,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.night_result_shield_available, charges),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
private fun MilestoneCard(milestone: Int) {
    NeonCard(
        glowColor = NeonColors.NeonPurple,
        glowIntensity = 0.3f,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.night_result_next_milestone, milestone),
                style = MaterialTheme.typography.bodyMedium,
                color = NeonColors.NeonPurple,
            )
        }
    }
}

@Composable
private fun ShareButtonTeaser() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedButton(
            onClick = { /* No-op - disabled for MVP */ },
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                disabledContentColor = NeonColors.TextMuted,
            ),
        ) {
            Text(text = stringResource(Res.string.night_result_share))
        }
        Text(
            text = stringResource(Res.string.night_result_share_coming_soon),
            style = MaterialTheme.typography.bodySmall,
            color = NeonColors.TextMuted,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun BreakdownCard(result: NightResult) {
    var expanded by remember { mutableStateOf(false) }

    NeonCard(
        glowColor = NeonColors.NeonYellow,
        glowIntensity = 0.2f,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.night_result_breakdown_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonColors.TextPrimary,
                )
                Text(
                    text = if (expanded) "-" else "+",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonColors.NeonYellow,
                )
            }
            if (expanded) {
                BreakdownRow(
                    label = stringResource(Res.string.night_result_xp_base),
                    value = result.xpBreakdown.baseXp,
                )
                BreakdownRow(
                    label = stringResource(Res.string.night_result_xp_score_bonus),
                    value = result.xpBreakdown.scoreBonusXp,
                )
                BreakdownRow(
                    label = stringResource(Res.string.night_result_xp_perfect),
                    value = result.xpBreakdown.perfectBonusXp,
                )
                BreakdownRow(
                    label = stringResource(Res.string.night_result_xp_talent_bonus),
                    value = result.xpBreakdown.talentAdditionsXp,
                )
                BreakdownRowText(
                    label = stringResource(Res.string.night_result_xp_streak_multiplier),
                    value = stringResource(
                        Res.string.night_result_xp_multiplier_value,
                        result.xpBreakdown.streakMultiplier,
                    ),
                )
                BreakdownRowText(
                    label = stringResource(Res.string.night_result_xp_talent_multiplier),
                    value = stringResource(
                        Res.string.night_result_xp_multiplier_value,
                        result.xpBreakdown.talentMultiplier,
                    ),
                )
            }
            // Total with animated counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.night_result_xp_total),
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonColors.NeonYellow,
                )
                AnimatedCounter(
                    targetValue = result.xpBreakdown.totalXp,
                    durationMillis = 1500,
                ) { currentValue ->
                    Text(
                        text = "+$currentValue XP",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonColors.NeonYellow,
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextPrimary,
        )
    }
}

@Composable
private fun BreakdownRowText(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextPrimary,
        )
    }
}

@Composable
private fun statusLabel(status: NightStatus): String {
    return when (status) {
        NightStatus.SUCCESS -> stringResource(Res.string.night_result_label_success)
        NightStatus.PARTIAL -> stringResource(Res.string.night_result_label_partial)
        NightStatus.FAIL -> stringResource(Res.string.night_result_label_fail)
        NightStatus.IN_PROGRESS -> stringResource(Res.string.night_result_label_in_progress)
        NightStatus.VOID -> stringResource(Res.string.night_result_label_fail)
    }
}

private fun statusColor(status: NightStatus): Color {
    return when (status) {
        NightStatus.SUCCESS -> NeonColors.NeonGreen
        NightStatus.PARTIAL -> NeonColors.StatusPartial
        NightStatus.FAIL -> NeonColors.StatusFail
        NightStatus.IN_PROGRESS -> NeonColors.NeonElectricBlue
        NightStatus.VOID -> NeonColors.StatusFail
    }
}

@Composable
private fun coachMessage(status: NightStatus, style: CoachStyle): String {
    return when (status) {
        NightStatus.SUCCESS -> when (style) {
            CoachStyle.CHILL -> stringResource(Res.string.night_result_success_chill)
            CoachStyle.HYPE -> stringResource(Res.string.night_result_success_hype)
            CoachStyle.STRICT -> stringResource(Res.string.night_result_success_strict)
        }
        NightStatus.PARTIAL -> when (style) {
            CoachStyle.CHILL -> stringResource(Res.string.night_result_partial_chill)
            CoachStyle.HYPE -> stringResource(Res.string.night_result_partial_hype)
            CoachStyle.STRICT -> stringResource(Res.string.night_result_partial_strict)
        }
        NightStatus.FAIL -> when (style) {
            CoachStyle.CHILL -> stringResource(Res.string.night_result_fail_chill)
            CoachStyle.HYPE -> stringResource(Res.string.night_result_fail_hype)
            CoachStyle.STRICT -> stringResource(Res.string.night_result_fail_strict)
        }
        NightStatus.IN_PROGRESS -> stringResource(Res.string.night_result_partial_chill)
        NightStatus.VOID -> stringResource(Res.string.night_result_fail_chill)
    }
}
