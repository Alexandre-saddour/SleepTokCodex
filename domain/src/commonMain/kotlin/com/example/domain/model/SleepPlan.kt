package com.example.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

data class SleepPlan(
    val id: Long,
    val userId: Long,
    val planStartLocalTime: LocalTime,
    val planEndLocalTime: LocalTime,
    val activeDaysMask: Int,
    val toleranceStartMinutes: Int,
    val toleranceEndMinutes: Int,
    val createdAt: Instant,
    val isActive: Boolean,
)
