package com.example.domain.model

data class HomeSummary(
    val user: User,
    val plan: SleepPlan,
    val activeNight: Night?,
)
