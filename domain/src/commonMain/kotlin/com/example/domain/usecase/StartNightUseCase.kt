package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.repository.NightRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import kotlinx.datetime.Instant

class StartNightUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(startAt: Instant): AppResult<Night> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val plan = sleepPlanRepository.getActivePlan(user.id).getOrThrow()
                ?: return AppResult.Error(DomainError.NotFound)

            val night = Night(
                id = 0L,
                userId = user.id,
                planId = plan.id,
                startAt = startAt,
                endAt = null,
                status = NightStatus.IN_PROGRESS,
                actualDurationMinutes = null,
                planDurationMinutes = plan.durationMinutes,
                score = null,
                xpEarned = null,
                streakBefore = user.streakCurrent,
                streakAfter = null,
                createdAt = startAt,
                note = null,
            )
            nightRepository.createNight(night)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
