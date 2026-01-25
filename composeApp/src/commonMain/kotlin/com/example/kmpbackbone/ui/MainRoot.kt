package com.example.kmpbackbone.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kmpbackbone.navigation.AppTab
import com.example.kmpbackbone.ui.home.HomeRoot
import com.example.kmpbackbone.ui.progress.ProgressRoot
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
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab) },
                        icon = {},
                        label = { Text(text = stringResource(tabLabelRes(tab))) },
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (selectedTab) {
                AppTab.HOME -> HomeRoot(
                    onOpenResult = onOpenNightResult,
                    onEditPlan = onEditPlan,
                )
                AppTab.PROGRESS -> ProgressRoot()
                AppTab.TALENTS -> Box(modifier = Modifier.fillMaxSize())
                AppTab.PROFILE -> Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

private fun tabLabelRes(tab: AppTab) = when (tab) {
    AppTab.HOME -> Res.string.tab_home
    AppTab.PROGRESS -> Res.string.tab_progress
    AppTab.TALENTS -> Res.string.tab_talents
    AppTab.PROFILE -> Res.string.tab_profile
}
