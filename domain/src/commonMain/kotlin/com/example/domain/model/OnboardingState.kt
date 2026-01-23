package com.example.domain.model

data class OnboardingState(
    val isComplete: Boolean,
    val user: User?,
    val plan: SleepPlan?,
)
