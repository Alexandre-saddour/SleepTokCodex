package com.example.domain.usecase

import com.example.domain.model.HomeSummary
import com.example.domain.repository.NightRepository
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError

class GetHomeSummaryUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(): AppResult<HomeSummary> {
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
        val activeNightResult = nightRepository.getActiveNight(user.id)
        if (activeNightResult is AppResult.Error) {
            return activeNightResult
        }
        val activeNight = (activeNightResult as AppResult.Success).value
        return AppResult.Success(HomeSummary(user = user, plan = plan, activeNight = activeNight))
    }
}
