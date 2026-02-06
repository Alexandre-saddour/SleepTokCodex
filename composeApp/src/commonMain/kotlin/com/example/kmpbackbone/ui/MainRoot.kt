package com.example.kmpbackbone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.kmpbackbone.navigation.AppTab
import com.example.kmpbackbone.ui.dailychest.DailyChestRoot
import com.example.kmpbackbone.ui.home.HomeRoot
import com.example.kmpbackbone.ui.profile.ProfileRoot
import com.example.kmpbackbone.ui.progress.ProgressRoot
import com.example.kmpbackbone.ui.talents.TalentsRoot
import com.example.kmpbackbone.ui.theme.NeonColors
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.tab_home
import kmpbackbone.composeapp.generated.resources.tab_profile
import kmpbackbone.composeapp.generated.resources.tab_progress
import kmpbackbone.composeapp.generated.resources.tab_talents
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainRoot(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onOpenNightResult: (Long) -> Unit,
    onEditPlan: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    var showDailyChest by remember { mutableStateOf(false) }
    var onDailyChestDismiss by remember { mutableStateOf<(() -> Unit)?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.NeonDarkBackground),
    ) {
        // Main content area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when (selectedTab) {
                AppTab.HOME -> HomeRoot(
                    onOpenResult = onOpenNightResult,
                    onEditPlan = onEditPlan,
                    onOpenDailyChest = { refreshCallback ->
                        showDailyChest = true
                        onDailyChestDismiss = refreshCallback
                    },
                )
                AppTab.PROGRESS -> ProgressRoot()
                AppTab.TALENTS -> TalentsRoot()
                AppTab.PROFILE -> ProfileRoot(
                    onOpenSettings = onOpenSettings,
                )
            }
        }

        // Neon bottom navigation
        NeonBottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
        )
    }

    when {
        showDailyChest -> DailyChestRoot(
            onDismiss = {
                showDailyChest = false
                onDailyChestDismiss?.invoke()
                onDailyChestDismiss = null
            },
        )
    }
}

@Composable
private fun NeonBottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
    ) {
        // Top edge glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            NeonColors.NeonElectricBlue.copy(alpha = 0f),
                            NeonColors.NeonElectricBlue.copy(alpha = 0.5f),
                            NeonColors.NeonElectricBlue.copy(alpha = 0f),
                        ),
                    ),
                ),
        )

        // Navigation surface
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = NeonColors.NeonDarkSurface.copy(alpha = 0.95f),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppTab.entries.forEach { tab ->
                    NeonNavItem(
                        tab = tab,
                        isSelected = tab == selectedTab,
                        onSelected = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun NeonNavItem(
    tab: AppTab,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabColor = when {
        isSelected -> NeonColors.NeonElectricBlue
        else -> NeonColors.TextMuted
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .selectable(
                selected = isSelected,
                onClick = onSelected,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Glow indicator for selected tab
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .height(3.dp)
                        .fillMaxWidth(0.5f)
                        .blur(4.dp)
                        .background(
                            NeonColors.NeonElectricBlue.copy(alpha = 0.8f),
                            RoundedCornerShape(2.dp),
                        ),
                )
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth(0.4f)
                        .background(
                            NeonColors.NeonElectricBlue,
                            RoundedCornerShape(2.dp),
                        ),
                )
            }

            Text(
                text = stringResource(tabLabelRes(tab)),
                style = MaterialTheme.typography.labelMedium,
                color = tabColor,
                modifier = Modifier.padding(top = if (isSelected) 6.dp else 10.dp),
            )
        }
    }
}

private fun tabLabelRes(tab: AppTab) = when (tab) {
    AppTab.HOME -> Res.string.tab_home
    AppTab.PROGRESS -> Res.string.tab_progress
    AppTab.TALENTS -> Res.string.tab_talents
    AppTab.PROFILE -> Res.string.tab_profile
}
