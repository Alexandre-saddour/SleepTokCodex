package com.example.domain.usecase

import com.example.domain.model.SleepPlan
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.result.AppResult

class UpdatePlanUseCase(
    private val sleepPlanRepository: SleepPlanRepository,
) {
    suspend fun execute(plan: SleepPlan): AppResult<Unit> {
        return sleepPlanRepository.updatePlan(plan)
    }
}
