package com.example.kmpbackbone.navigation

sealed interface AppRoute {
    data object Onboarding : AppRoute
    data class Main(val tab: AppTab) : AppRoute
    data class NightResult(val nightId: Long) : AppRoute
    data object Settings : AppRoute
}
