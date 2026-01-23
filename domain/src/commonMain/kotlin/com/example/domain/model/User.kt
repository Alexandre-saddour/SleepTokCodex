package com.example.domain.model

import kotlinx.datetime.Instant

data class User(
    val id: Long,
    val createdAt: Instant,
    val timezone: String,
    val coachStyle: CoachStyle,
    val premiumStatus: PremiumStatus,
    val premiumUntil: Instant?,
    val level: Int,
    val xpTotal: Long,
    val talentPointsAvailable: Int,
    val streakCurrent: Int,
    val streakBest: Int,
    val lastNightId: Long?,
    val baselineSleepDurationMinutes: Int,
    val settingsJson: String?,
)
