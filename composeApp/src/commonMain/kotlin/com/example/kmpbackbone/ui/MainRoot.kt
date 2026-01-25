package com.example.kmpbackbone.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kmpbackbone.navigation.AppTab
import com.example.kmpbackbone.ui.home.HomeRoot

@Composable
fun MainRoot(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onOpenNightResult: (Long) -> Unit,
    onEditPlan: () -> Unit = {},
) {
    when (selectedTab) {
        AppTab.HOME -> HomeRoot(
            onOpenResult = onOpenNightResult,
            onEditPlan = onEditPlan,
        )
        AppTab.PROGRESS -> Box(modifier = Modifier.fillMaxSize())
        AppTab.TALENTS -> Box(modifier = Modifier.fillMaxSize())
        AppTab.PROFILE -> Box(modifier = Modifier.fillMaxSize())
    }
}
