package com.example.domain.usecase

import com.example.domain.model.SleepPlan
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class GetActivePlanUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
) {
    suspend fun execute(): AppResult<SleepPlan?> {
        return when (val userResult = userRepository.getActiveUser()) {
            is AppResult.Error -> userResult
            is AppResult.Success -> sleepPlanRepository.getActivePlan(userResult.value.id)
        }
    }
}
