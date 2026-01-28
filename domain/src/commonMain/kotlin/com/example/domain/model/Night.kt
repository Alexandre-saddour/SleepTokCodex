package com.example.domain.model

import kotlin.time.Instant

data class Night(
    val id: Long,
    val userId: Long,
    val planId: Long,
    val startAt: Instant,
    val endAt: Instant?,
    val status: NightStatus,
    val actualDurationMinutes: Int?,
    val planDurationMinutes: Int,
    val score: Int?,
    val xpEarned: Int?,
    val streakBefore: Int?,
    val streakAfter: Int?,
    val createdAt: Instant,
    val note: String?,
)
