package com.example.kmpbackbone.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.domain.result.DomainError
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonCard
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.components.NeonProgressBar
import com.example.kmpbackbone.ui.theme.NeonColors
import com.example.kmpbackbone.viewmodel.ProfileBadgeUi
import com.example.kmpbackbone.viewmodel.ProfileStatsUi
import com.example.kmpbackbone.viewmodel.ProfileUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.profile_badges_title
import kmpbackbone.composeapp.generated.resources.profile_badges_unlocked
import kmpbackbone.composeapp.generated.resources.profile_error_generic
import kmpbackbone.composeapp.generated.resources.profile_error_not_found
import kmpbackbone.composeapp.generated.resources.profile_error_storage
import kmpbackbone.composeapp.generated.resources.profile_error_validation
import kmpbackbone.composeapp.generated.resources.profile_level_label
import kmpbackbone.composeapp.generated.resources.profile_level_short
import kmpbackbone.composeapp.generated.resources.profile_loading
import kmpbackbone.composeapp.generated.resources.profile_open_settings
import kmpbackbone.composeapp.generated.resources.profile_retry
import kmpbackbone.composeapp.generated.resources.profile_stat_best_streak
import kmpbackbone.composeapp.generated.resources.profile_stat_nights
import kmpbackbone.composeapp.generated.resources.profile_stat_wins
import kmpbackbone.composeapp.generated.resources.profile_stats_title
import kmpbackbone.composeapp.generated.resources.profile_title
import kmpbackbone.composeapp.generated.resources.profile_xp_progress
import kmpbackbone.composeapp.generated.resources.profile_xp_total
import org.jetbrains.compose.resources.stringResource

private const val badgeColumns = 3

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    NeonGradientBackground(
        modifier = Modifier.padding(20.dp),
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error, onRefresh)
            uiState.stats != null -> ProfileContent(uiState.stats, uiState.badges, onOpenSettings)
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
            text = stringResource(Res.string.profile_loading),
            style = MaterialTheme.typography.bodyLarge,
            color = NeonColors.TextSecondary,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ErrorState(error: DomainError, onRefresh: () -> Unit) {
    val message = when (error) {
        DomainError.NotFound -> Res.string.profile_error_not_found
        DomainError.Validation -> Res.string.profile_error_validation
        DomainError.Storage -> Res.string.profile_error_storage
        DomainError.Conflict -> Res.string.profile_error_generic
        DomainError.Unknown -> Res.string.profile_error_generic
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
        )
        NeonButton(
            onClick = onRefresh,
            modifier = Modifier.padding(top = 12.dp),
            glowColor = NeonColors.NeonElectricBlue,
        ) {
            Text(
                text = stringResource(Res.string.profile_retry),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun ProfileContent(
    stats: ProfileStatsUi,
    badges: List<ProfileBadgeUi>,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.profile_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonColors.TextPrimary,
            )
            Surface(
                onClick = onOpenSettings,
                shape = MaterialTheme.shapes.medium,
                color = NeonColors.NeonElectricBlue.copy(alpha = 0.1f),
                contentColor = NeonColors.NeonElectricBlue,
            ) {
                Text(
                    text = stringResource(Res.string.profile_open_settings),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
        ProfileHeader(stats)
        QuickStats(stats)
        BadgeGrid(badges)
    }
}

@Composable
private fun ProfileHeader(stats: ProfileStatsUi) {
    val progress = (stats.xpInLevel.toFloat() / stats.levelSpan.coerceAtLeast(1L)).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Neon level ring
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Glow
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .blur(12.dp)
                    .background(
                        NeonColors.NeonPurple.copy(alpha = 0.4f),
                        CircleShape,
                    ),
            )
            // Ring background
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(NeonColors.NeonDarkSurface, CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            0f to NeonColors.NeonPurple.copy(alpha = 0.2f),
                            progress to NeonColors.NeonPurple,
                            progress to NeonColors.NeonDarkSurfaceVariant,
                            1f to NeonColors.NeonDarkSurfaceVariant,
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.profile_level_short, stats.level),
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonColors.NeonPurple,
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.profile_level_label, stats.level),
                style = MaterialTheme.typography.titleLarge,
                color = NeonColors.TextPrimary,
            )
            NeonProgressBar(
                progress = progress,
                progressColor = NeonColors.NeonPurple,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = stringResource(Res.string.profile_xp_progress, stats.xpInLevel, stats.levelSpan),
                style = MaterialTheme.typography.bodySmall,
                color = NeonColors.TextSecondary,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = stringResource(Res.string.profile_xp_total, stats.xpTotal),
                style = MaterialTheme.typography.bodySmall,
                color = NeonColors.NeonYellow,
            )
        }
    }
}

@Composable
private fun QuickStats(stats: ProfileStatsUi) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.profile_stats_title),
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )
        NeonCard(
            glowColor = NeonColors.NeonElectricBlue,
            glowIntensity = 0.2f,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatItem(
                    label = stringResource(Res.string.profile_stat_nights),
                    value = stats.totalNights.toString(),
                    color = NeonColors.NeonElectricBlue,
                )
                StatItem(
                    label = stringResource(Res.string.profile_stat_wins),
                    value = stats.totalWins.toString(),
                    color = NeonColors.NeonGreen,
                )
                StatItem(
                    label = stringResource(Res.string.profile_stat_best_streak),
                    value = stats.bestStreak.toString(),
                    color = NeonColors.NeonYellow,
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = NeonColors.TextSecondary,
        )
    }
}

@Composable
private fun BadgeGrid(badges: List<ProfileBadgeUi>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val unlockedCount = badges.count { it.isUnlocked }
        Text(
            text = stringResource(Res.string.profile_badges_title),
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )
        Text(
            text = stringResource(Res.string.profile_badges_unlocked, unlockedCount, badges.size),
            style = MaterialTheme.typography.bodySmall,
            color = NeonColors.TextSecondary,
        )
        badges.chunked(badgeColumns).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { badge ->
                    NeonBadgeCell(
                        badge = badge,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size < badgeColumns) {
                    repeat(badgeColumns - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun NeonBadgeCell(badge: ProfileBadgeUi, modifier: Modifier) {
    val backgroundColor = when {
        badge.isUnlocked -> NeonColors.NeonPink.copy(alpha = 0.15f)
        else -> NeonColors.NeonDarkSurfaceVariant
    }
    val borderColor = when {
        badge.isUnlocked -> NeonColors.NeonPink.copy(alpha = 0.5f)
        else -> NeonColors.Outline
    }

    Box(
        modifier = modifier
            .height(72.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        if (badge.isUnlocked) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .blur(8.dp)
                    .background(NeonColors.NeonPink.copy(alpha = 0.5f), CircleShape),
            )
        }
    }
}
