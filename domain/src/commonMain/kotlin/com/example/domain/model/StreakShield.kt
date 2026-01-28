package com.example.domain.model

import kotlin.time.Instant

data class StreakShield(
    val userId: Long,
    val chargesAvailable: Int,
    val refreshAt: Instant,
    val source: ShieldSource,
)
