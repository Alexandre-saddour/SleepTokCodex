package com.example.kmpbackbone.ui.talents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.TalentBranch
import com.example.kmpbackbone.viewmodel.TalentsUiState
import com.example.kmpbackbone.viewmodel.TalentNodeUi
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.talents_loading
import kmpbackbone.composeapp.generated.resources.talents_title
import kmpbackbone.composeapp.generated.resources.talents_points_available
import kmpbackbone.composeapp.generated.resources.talents_unlock
import kmpbackbone.composeapp.generated.resources.talents_unlocked
import kmpbackbone.composeapp.generated.resources.talents_locked
import kmpbackbone.composeapp.generated.resources.branch_discipline
import kmpbackbone.composeapp.generated.resources.branch_streak
import kmpbackbone.composeapp.generated.resources.branch_style
import kmpbackbone.composeapp.generated.resources.branch_insight
import kmpbackbone.composeapp.generated.resources.talent_d1_name
import kmpbackbone.composeapp.generated.resources.talent_d1_desc
import kmpbackbone.composeapp.generated.resources.talent_d2_name
import kmpbackbone.composeapp.generated.resources.talent_d2_desc
import kmpbackbone.composeapp.generated.resources.talent_d3_name
import kmpbackbone.composeapp.generated.resources.talent_d3_desc
import kmpbackbone.composeapp.generated.resources.talent_s1_name
import kmpbackbone.composeapp.generated.resources.talent_s1_desc
import kmpbackbone.composeapp.generated.resources.talent_s2_name
import kmpbackbone.composeapp.generated.resources.talent_s2_desc
import kmpbackbone.composeapp.generated.resources.talent_s3_name
import kmpbackbone.composeapp.generated.resources.talent_s3_desc
import kmpbackbone.composeapp.generated.resources.talent_t1_name
import kmpbackbone.composeapp.generated.resources.talent_t1_desc
import kmpbackbone.composeapp.generated.resources.talent_t2_name
import kmpbackbone.composeapp.generated.resources.talent_t2_desc
import kmpbackbone.composeapp.generated.resources.talent_t3_name
import kmpbackbone.composeapp.generated.resources.talent_t3_desc
import kmpbackbone.composeapp.generated.resources.talent_i1_name
import kmpbackbone.composeapp.generated.resources.talent_i1_desc
import kmpbackbone.composeapp.generated.resources.talent_i2_name
import kmpbackbone.composeapp.generated.resources.talent_i2_desc
import kmpbackbone.composeapp.generated.resources.talent_i3_name
import kmpbackbone.composeapp.generated.resources.talent_i3_desc
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.StringResource

@Composable
fun TalentsScreen(
    uiState: TalentsUiState,
    onUnlock: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.talents_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(Res.string.talents_points_available, uiState.availablePoints),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text(
                text = stringResource(Res.string.talents_loading),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            TalentBranch.values().forEach { branch ->
                TalentBranchSection(
                    branch = branch,
                    talents = uiState.talents.filter { it.talent.branch == branch },
                    onUnlock = onUnlock,
                    isUnlocking = uiState.isUnlocking,
                )
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(branchLabel(branch)),
            style = MaterialTheme.typography.titleLarge,
        )
        talents.sortedBy { it.talent.tier.ordinal }.forEach { node ->
            TalentCard(
                node = node,
                onUnlock = onUnlock,
                isUnlocking = isUnlocking,
            )
        }
    }
}

@Composable
private fun TalentCard(
    node: TalentNodeUi,
    onUnlock: (String) -> Unit,
    isUnlocking: Boolean,
) {
    val talent = node.talent
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
                text = stringResource(talentNameRes(talent.nameKey)),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(talentDescRes(talent.descriptionKey)),
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.talents_cost, talent.costPoints),
                    style = MaterialTheme.typography.labelLarge,
                )
                when {
                    node.isUnlocked -> Text(
                        text = stringResource(Res.string.talents_unlocked),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    node.isUnlockable -> Button(
                        onClick = { onUnlock(talent.id) },
                        enabled = !isUnlocking,
                    ) {
                        Text(text = stringResource(Res.string.talents_unlock))
                    }
                    else -> OutlinedButton(
                        onClick = {},
                        enabled = false,
                    ) {
                        Text(text = stringResource(Res.string.talents_locked))
                    }
                }
            }
        }
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
        else -> Res.string.talents_title
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
        else -> Res.string.talents_title
    }
}
