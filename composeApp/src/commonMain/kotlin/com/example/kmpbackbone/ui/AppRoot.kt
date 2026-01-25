package com.example.kmpbackbone.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.kmpbackbone.navigation.AppNavigator
import com.example.kmpbackbone.navigation.AppRoute
import com.example.kmpbackbone.ui.theme.AppTheme
import com.example.kmpbackbone.ui.nightresult.NightResultRoot

@Composable
fun AppRoot() {
    AppTheme {
        val navigator = remember { AppNavigator() }
        when (val route = navigator.route) {
            AppRoute.Onboarding -> OnboardingRoot(onCompleted = { navigator.goToMain() })
            is AppRoute.Main -> MainRoot(
                selectedTab = route.tab,
                onTabSelected = { navigator.goToMain(it) },
                onOpenNightResult = { nightId -> navigator.goToNightResult(nightId) },
            )
            is AppRoute.NightResult -> NightResultRoot(
                nightId = route.nightId,
                onBack = { navigator.goToMain() },
            )
        }
    }
}
