package com.example.kmpbackbone.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppNavigator(
    initialRoute: AppRoute = AppRoute.Onboarding,
) {
    var route: AppRoute by mutableStateOf(initialRoute)
        private set

    private val backStack = mutableListOf<AppRoute>()

    fun goToOnboarding() {
        backStack.clear()
        route = AppRoute.Onboarding
    }

    fun goToMain(tab: AppTab = AppTab.HOME) {
        // If we are already on Main, just switch tab
        val current = route
        if (current is AppRoute.Main) {
            route = current.copy(tab = tab)
            return
        }
        // Otherwise, clear stack (Main is root) and go to Main
        backStack.clear()
        route = AppRoute.Main(tab)
    }

    fun goToNightResult(nightId: Long) {
        backStack.add(route)
        route = AppRoute.NightResult(nightId)
    }

    fun goToSettings() {
        backStack.add(route)
        route = AppRoute.Settings
    }

    fun goBack() {
        if (backStack.isNotEmpty()) {
            route = backStack.removeLast()
        }
    }
}
