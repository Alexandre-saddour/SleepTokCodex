package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.repository.NightRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

class StartNightUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(startAt: Instant): AppResult<Night> {
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
        val planDurationMinutes = computePlanDurationMinutes(plan.planStartLocalTime, plan.planEndLocalTime)
        val night = Night(
            id = 0L,
            userId = user.id,
            planId = plan.id,
            startAt = startAt,
            endAt = null,
            status = NightStatus.IN_PROGRESS,
            actualDurationMinutes = null,
            planDurationMinutes = planDurationMinutes,
            score = null,
            xpEarned = null,
            streakBefore = user.streakCurrent,
            streakAfter = null,
            createdAt = startAt,
            note = null,
        )
        return nightRepository.createNight(night)
    }

    private fun computePlanDurationMinutes(start: LocalTime, end: LocalTime): Int {
        val startMinutes = start.hour * 60 + start.minute
        val endMinutes = end.hour * 60 + end.minute
        return if (endMinutes >= startMinutes) {
            endMinutes - startMinutes
        } else {
            (24 * 60 - startMinutes) + endMinutes
        }
    }
}
