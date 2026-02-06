package com.example.kmpbackbone.ui.talents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.TalentBranch
import com.example.kmpbackbone.ui.components.NeonBadge
import com.example.kmpbackbone.ui.components.NeonButton
import com.example.kmpbackbone.ui.components.NeonGradientBackground
import com.example.kmpbackbone.ui.components.NeonSectionHeader
import com.example.kmpbackbone.ui.components.NeonTalentCard
import com.example.kmpbackbone.ui.theme.NeonColors
import com.example.kmpbackbone.viewmodel.TalentNodeUi
import com.example.kmpbackbone.viewmodel.TalentsUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.branch_discipline
import kmpbackbone.composeapp.generated.resources.branch_insight
import kmpbackbone.composeapp.generated.resources.branch_streak
import kmpbackbone.composeapp.generated.resources.branch_style
import kmpbackbone.composeapp.generated.resources.talent_d1_desc
import kmpbackbone.composeapp.generated.resources.talent_d1_name
import kmpbackbone.composeapp.generated.resources.talent_d2_desc
import kmpbackbone.composeapp.generated.resources.talent_d2_name
import kmpbackbone.composeapp.generated.resources.talent_d3_desc
import kmpbackbone.composeapp.generated.resources.talent_d3_name
import kmpbackbone.composeapp.generated.resources.talent_i1_desc
import kmpbackbone.composeapp.generated.resources.talent_i1_name
import kmpbackbone.composeapp.generated.resources.talent_i2_desc
import kmpbackbone.composeapp.generated.resources.talent_i2_name
import kmpbackbone.composeapp.generated.resources.talent_i3_desc
import kmpbackbone.composeapp.generated.resources.talent_i3_name
import kmpbackbone.composeapp.generated.resources.talent_s1_desc
import kmpbackbone.composeapp.generated.resources.talent_s1_name
import kmpbackbone.composeapp.generated.resources.talent_s2_desc
import kmpbackbone.composeapp.generated.resources.talent_s2_name
import kmpbackbone.composeapp.generated.resources.talent_s3_desc
import kmpbackbone.composeapp.generated.resources.talent_s3_name
import kmpbackbone.composeapp.generated.resources.talent_t1_desc
import kmpbackbone.composeapp.generated.resources.talent_t1_name
import kmpbackbone.composeapp.generated.resources.talent_t2_desc
import kmpbackbone.composeapp.generated.resources.talent_t2_name
import kmpbackbone.composeapp.generated.resources.talent_t3_desc
import kmpbackbone.composeapp.generated.resources.talent_t3_name
import kmpbackbone.composeapp.generated.resources.talents_cost
import kmpbackbone.composeapp.generated.resources.talents_loading
import kmpbackbone.composeapp.generated.resources.talents_locked
import kmpbackbone.composeapp.generated.resources.talents_points_available
import kmpbackbone.composeapp.generated.resources.talents_title
import kmpbackbone.composeapp.generated.resources.talents_unlock
import kmpbackbone.composeapp.generated.resources.talents_unlocked
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TalentsScreen(
    uiState: TalentsUiState,
    onUnlock: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    NeonGradientBackground(
        modifier = Modifier.padding(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.talents_title),
                style = MaterialTheme.typography.headlineMedium,
                color = NeonColors.TextPrimary,
            )
            NeonBadge(
                text = stringResource(Res.string.talents_points_available, uiState.availablePoints),
                color = NeonColors.NeonYellow,
            )
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = NeonColors.NeonElectricBlue,
                )
                Text(
                    text = stringResource(Res.string.talents_loading),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonColors.TextSecondary,
                )
            } else {
                val talentsByBranch = uiState.talents.groupBy { it.talent.branch }
                TalentBranch.entries.forEach { branch ->
                    TalentBranchSection(
                        branch = branch,
                        talents = talentsByBranch[branch].orEmpty(),
                        onUnlock = onUnlock,
                        isUnlocking = uiState.isUnlocking,
                    )
                }
            }
        }
    }
}

@Composable
private fun TalentBranchSection(
    branch: TalentBranch,
    talents: List<TalentNodeUi>,
    onUnlock: (String) -> Unit,
    isUnlocking: Boolean,
) {
    val branchColor = branchColor(branch)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NeonSectionHeader(
            text = stringResource(branchLabel(branch)),
            accentColor = branchColor,
        )
        talents.sortedBy { it.talent.tier.ordinal }.forEach { node ->
            NeonTalentCardItem(
                node = node,
                branchColor = branchColor,
                onUnlock = onUnlock,
                isUnlocking = isUnlocking,
            )
        }
    }
}

@Composable
private fun NeonTalentCardItem(
    node: TalentNodeUi,
    branchColor: Color,
    onUnlock: (String) -> Unit,
    isUnlocking: Boolean,
) {
    val talent = node.talent

    NeonTalentCard(
        title = stringResource(talentNameRes(talent.nameKey)),
        description = stringResource(talentDescRes(talent.descriptionKey)),
        branchColor = branchColor,
        isUnlocked = node.isUnlocked,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.talents_cost, talent.costPoints),
                style = MaterialTheme.typography.labelLarge,
                color = NeonColors.TextSecondary,
            )
            when {
                node.isUnlocked -> NeonBadge(
                    text = stringResource(Res.string.talents_unlocked),
                    color = branchColor,
                )
                node.isUnlockable -> NeonButton(
                    onClick = { onUnlock(talent.id) },
                    enabled = !isUnlocking,
                    glowColor = branchColor,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.talents_unlock),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                else -> Surface(
                    color = NeonColors.NeonDarkSurfaceVariant,
                    contentColor = NeonColors.TextMuted,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.talents_locked),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}

private fun branchColor(branch: TalentBranch): Color {
    return when (branch) {
        TalentBranch.DISCIPLINE -> NeonColors.BranchDiscipline
        TalentBranch.STREAK -> NeonColors.BranchStreak
        TalentBranch.STYLE -> NeonColors.BranchStyle
        TalentBranch.INSIGHT -> NeonColors.BranchInsight
    }
}

private fun branchLabel(branch: TalentBranch): StringResource {
    return when (branch) {
        TalentBranch.DISCIPLINE -> Res.string.branch_discipline
        TalentBranch.STREAK -> Res.string.branch_streak
        TalentBranch.STYLE -> Res.string.branch_style
        TalentBranch.INSIGHT -> Res.string.branch_insight
    }
}

private fun talentNameRes(key: String): StringResource {
    return when (key) {
        "talent_d1_name" -> Res.string.talent_d1_name
        "talent_d2_name" -> Res.string.talent_d2_name
        "talent_d3_name" -> Res.string.talent_d3_name
        "talent_s1_name" -> Res.string.talent_s1_name
        "talent_s2_name" -> Res.string.talent_s2_name
        "talent_s3_name" -> Res.string.talent_s3_name
        "talent_t1_name" -> Res.string.talent_t1_name
        "talent_t2_name" -> Res.string.talent_t2_name
        "talent_t3_name" -> Res.string.talent_t3_name
        "talent_i1_name" -> Res.string.talent_i1_name
        "talent_i2_name" -> Res.string.talent_i2_name
        "talent_i3_name" -> Res.string.talent_i3_name
        else -> throw IllegalArgumentException("Unknown talent name key: $key")
    }
}

private fun talentDescRes(key: String): StringResource {
    return when (key) {
        "talent_d1_desc" -> Res.string.talent_d1_desc
        "talent_d2_desc" -> Res.string.talent_d2_desc
        "talent_d3_desc" -> Res.string.talent_d3_desc
        "talent_s1_desc" -> Res.string.talent_s1_desc
        "talent_s2_desc" -> Res.string.talent_s2_desc
        "talent_s3_desc" -> Res.string.talent_s3_desc
        "talent_t1_desc" -> Res.string.talent_t1_desc
        "talent_t2_desc" -> Res.string.talent_t2_desc
        "talent_t3_desc" -> Res.string.talent_t3_desc
        "talent_i1_desc" -> Res.string.talent_i1_desc
        "talent_i2_desc" -> Res.string.talent_i2_desc
        "talent_i3_desc" -> Res.string.talent_i3_desc
        else -> throw IllegalArgumentException("Unknown talent description key: $key")
    }
}
