package com.example.domain.model

import kotlinx.datetime.Instant

data class StreakShield(
    val userId: Long,
    val chargesAvailable: Int,
    val refreshAt: Instant,
    val source: ShieldSource,
)
