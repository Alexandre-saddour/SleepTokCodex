package com.example.domain.usecase

import com.example.domain.model.NightStatus
import com.example.domain.model.SleepPlan
import com.example.domain.model.WeeklyRecap
import com.example.domain.repository.NightRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
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
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val plan = sleepPlanRepository.getActivePlan(user.id).getOrThrow()
                ?: return AppResult.Error(DomainError.NotFound)
            val nights = nightRepository.getNightsBetween(user.id, weekStart, weekEnd, timeZone).getOrThrow()

            val totalSleptMinutes = nights.sumOf { it.actualDurationMinutes ?: 0 }
            val scores = nights.mapNotNull { it.score }
            val averageScore = if (scores.isNotEmpty()) scores.sum() / scores.size else 0
            val successCount = nights.count { it.status == NightStatus.SUCCESS }
            val perfectCount = nights.count { it.status == NightStatus.SUCCESS && (it.score ?: 0) >= 90 }
            val activeDaysCount = countActiveDays(plan, weekStart, weekEnd)
            val targetMinutes = plan.durationMinutes * activeDaysCount
            val averageActual = if (nights.isNotEmpty()) totalSleptMinutes / nights.size else 0
            val sleepGained = averageActual - user.baselineSleepDurationMinutes

            AppResult.Success(
                WeeklyRecap(
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
            )
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }

    private fun countActiveDays(plan: SleepPlan, start: LocalDate, end: LocalDate): Int {
        var current = start
        var count = 0
        while (current <= end) {
            if (plan.isActiveDay(current.dayOfWeek)) {
                count++
            }
            current = current.plus(1, DateTimeUnit.DAY)
        }
        return count
    }
}
