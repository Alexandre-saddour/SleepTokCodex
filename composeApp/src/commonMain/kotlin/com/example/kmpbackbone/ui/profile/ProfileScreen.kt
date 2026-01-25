package com.example.kmpbackbone.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.domain.result.DomainError
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
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(uiState.error, onRefresh)
            uiState.stats != null -> ProfileContent(uiState.stats, uiState.badges)
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
            text = stringResource(Res.string.profile_loading),
            style = MaterialTheme.typography.bodyLarge,
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
        )
        Button(
            onClick = onRefresh,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text(text = stringResource(Res.string.profile_retry))
        }
    }
}

@Composable
private fun ProfileContent(stats: ProfileStatsUi, badges: List<ProfileBadgeUi>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        ProfileHeader(stats)
        QuickStats(stats)
        BadgeGrid(badges)
    }
}

@Composable
private fun ProfileHeader(stats: ProfileStatsUi) {
    val progress = (stats.xpInLevel.toFloat() / stats.levelSpan.coerceAtLeast(1)).coerceIn(0f, 1f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(Res.string.profile_level_short, stats.level),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.profile_level_label, stats.level),
                style = MaterialTheme.typography.titleLarge,
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
            Text(
                text = stringResource(Res.string.profile_xp_progress, stats.xpInLevel, stats.levelSpan),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = stringResource(Res.string.profile_xp_total, stats.xpTotal),
                style = MaterialTheme.typography.bodySmall,
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
        )
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
                StatItem(
                    label = stringResource(Res.string.profile_stat_nights),
                    value = stats.totalNights.toString(),
                )
                StatItem(
                    label = stringResource(Res.string.profile_stat_wins),
                    value = stats.totalWins.toString(),
                )
                StatItem(
                    label = stringResource(Res.string.profile_stat_best_streak),
                    value = stats.bestStreak.toString(),
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun BadgeGrid(badges: List<ProfileBadgeUi>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val unlockedCount = badges.count { it.isUnlocked }
        Text(
            text = stringResource(Res.string.profile_badges_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(Res.string.profile_badges_unlocked, unlockedCount, badges.size),
            style = MaterialTheme.typography.bodySmall,
        )
        badges.chunked(badgeColumns).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { badge ->
                    BadgeCell(
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
private fun BadgeCell(badge: ProfileBadgeUi, modifier: Modifier) {
    val background = if (badge.isUnlocked) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(background),
    )
}
