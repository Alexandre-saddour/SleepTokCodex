package com.example.domain.usecase

import com.example.domain.model.NightStatus
import com.example.domain.model.WeeklyRecap
import com.example.domain.model.SleepPlan
import com.example.domain.repository.NightRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class GetWeeklyRecapUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(weekStart: LocalDate, weekEnd: LocalDate, timeZone: TimeZone): AppResult<WeeklyRecap> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val planResult = sleepPlanRepository.getActivePlan(user.id)
        if (planResult is AppResult.Error) {
            return planResult
        }
        val plan = (planResult as AppResult.Success).value
            ?: return AppResult.Error(DomainError.NotFound)
        val nightsResult = nightRepository.getNightsBetween(user.id, weekStart, weekEnd, timeZone)
        if (nightsResult is AppResult.Error) {
            return nightsResult
        }
        val nights = (nightsResult as AppResult.Success).value
        val totalSleptMinutes = nights.sumOf { it.actualDurationMinutes ?: 0 }
        val scores = nights.mapNotNull { it.score }
        val averageScore = if (scores.isNotEmpty()) scores.sum() / scores.size else 0
        val successCount = nights.count { it.status == NightStatus.SUCCESS }
        val perfectCount = nights.count { it.status == NightStatus.SUCCESS && (it.score ?: 0) >= 90 }
        val activeDaysCount = countActiveDays(plan.activeDaysMask, weekStart, weekEnd)
        val targetMinutes = plan.planDurationMinutes() * activeDaysCount
        val averageActual = if (nights.isNotEmpty()) totalSleptMinutes / nights.size else 0
        val sleepGained = averageActual - user.baselineSleepDurationMinutes
        val recap = WeeklyRecap(
            weekStart = weekStart,
            weekEnd = weekEnd,
            totalSleptMinutes = totalSleptMinutes,
            targetMinutes = targetMinutes,
            sleepGainedMinutes = sleepGained,
            successCount = successCount,
            bestStreak = user.streakBest,
            averageScore = averageScore,
            perfectCount = perfectCount,
        )
        return AppResult.Success(recap)
    }

    private fun countActiveDays(mask: Int, start: LocalDate, end: LocalDate): Int {
        var current = start
        var count = 0
        while (current <= end) {
            if (isActiveDay(mask, current.dayOfWeek)) {
                count += 1
            }
            current = current.plus(1, DateTimeUnit.DAY)
        }
        return count
    }

    private fun isActiveDay(mask: Int, dayOfWeek: DayOfWeek): Boolean {
        val index = dayOfWeek.ordinal
        return mask and (1 shl index) != 0
    }

    private fun SleepPlan.planDurationMinutes(): Int {
        val startMinutes = planStartLocalTime.hour * 60 + planStartLocalTime.minute
        val endMinutes = planEndLocalTime.hour * 60 + planEndLocalTime.minute
        return if (endMinutes >= startMinutes) {
            endMinutes - startMinutes
        } else {
            (24 * 60 - startMinutes) + endMinutes
        }
    }
}
