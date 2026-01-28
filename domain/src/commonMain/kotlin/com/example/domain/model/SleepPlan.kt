package com.example.domain.model

import kotlin.time.Instant
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

    val durationMinutes: Int
        get() {
            val startMinutes = planStartLocalTime.hour * 60 + planStartLocalTime.minute
            val endMinutes = planEndLocalTime.hour * 60 + planEndLocalTime.minute
            return if (endMinutes >= startMinutes) {
                endMinutes - startMinutes
            } else {
                (24 * 60 - startMinutes) + endMinutes
            }
        }

    fun isActiveDay(dayOfWeek: DayOfWeek): Boolean = activeDays.contains(dayOfWeek)

    companion object {
        fun computeActiveDaysMask(days: Set<DayOfWeek>): Int {
            return days.fold(0) { mask, day -> mask or (1 shl day.ordinal) }
        }
    }
}

