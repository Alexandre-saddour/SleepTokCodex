package com.example.kmpbackbone.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kmpbackbone.navigation.AppTab
import com.example.kmpbackbone.ui.home.HomeScreen

@Composable
fun MainRoot(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    when (selectedTab) {
        AppTab.HOME -> HomeScreen()
        AppTab.PROGRESS -> Box(modifier = Modifier.fillMaxSize())
        AppTab.TALENTS -> Box(modifier = Modifier.fillMaxSize())
        AppTab.PROFILE -> Box(modifier = Modifier.fillMaxSize())
    }
}
