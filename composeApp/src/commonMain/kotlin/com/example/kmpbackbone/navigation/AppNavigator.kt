package com.example.kmpbackbone.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppNavigator(
    initialRoute: AppRoute = AppRoute.Onboarding,
) {
    var route: AppRoute by mutableStateOf(initialRoute)
        private set

    fun goToOnboarding() {
        route = AppRoute.Onboarding
    }

    fun goToMain(tab: AppTab = AppTab.HOME) {
        route = AppRoute.Main(tab)
    }
}
