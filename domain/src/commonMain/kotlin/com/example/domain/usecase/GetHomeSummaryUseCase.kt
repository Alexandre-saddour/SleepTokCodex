package com.example.domain.usecase

import com.example.domain.model.HomeSummary
import com.example.domain.repository.NightRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow

class GetHomeSummaryUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(): AppResult<HomeSummary> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val plan = sleepPlanRepository.getActivePlan(user.id).getOrThrow()
                ?: return AppResult.Error(DomainError.NotFound)
            val activeNight = nightRepository.getActiveNight(user.id).getOrThrow()

            AppResult.Success(HomeSummary(user = user, plan = plan, activeNight = activeNight))
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
