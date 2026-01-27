package com.example.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.DayOfWeek

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
) {
    val activeDays: Set<DayOfWeek>
        get() = DayOfWeek.entries.filter { day -> activeDaysMask and (1 shl day.ordinal) != 0 }.toSet()

    companion object {
        fun computeActiveDaysMask(days: Set<DayOfWeek>): Int {
            return days.fold(0) { mask, day -> mask or (1 shl day.ordinal) }
        }
    }
}

