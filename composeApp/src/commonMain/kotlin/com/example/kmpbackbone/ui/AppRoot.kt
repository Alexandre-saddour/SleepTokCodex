package com.example.kmpbackbone.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.kmpbackbone.navigation.AppNavigator
import com.example.kmpbackbone.navigation.AppRoute

@Composable
fun AppRoot() {
    MaterialTheme {
        val navigator = remember { AppNavigator() }
        when (val route = navigator.route) {
            AppRoute.Onboarding -> OnboardingRoot(onCompleted = { navigator.goToMain() })
            is AppRoute.Main -> MainRoot(
                selectedTab = route.tab,
                onTabSelected = { navigator.goToMain(it) },
            )
        }
    }
}
