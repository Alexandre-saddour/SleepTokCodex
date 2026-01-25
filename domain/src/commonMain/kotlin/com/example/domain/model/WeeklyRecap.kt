package com.example.domain.model

import kotlinx.datetime.LocalDate

data class WeeklyRecap(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val totalSleptMinutes: Int,
    val targetMinutes: Int,
    val sleepGainedMinutes: Int,
    val successCount: Int,
    val bestStreak: Int,
    val averageScore: Int,
    val perfectCount: Int,
)
