package com.example.domain.usecase

import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class CompleteOnboardingUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
) {
    suspend fun execute(user: User, plan: SleepPlan): AppResult<Unit> {
        val createdUserResult = userRepository.createUser(user)
        if (createdUserResult is AppResult.Error) {
            return createdUserResult
        }
        val createdUser = (createdUserResult as AppResult.Success).value
        val planToCreate = plan.copy(userId = createdUser.id)
        val createdPlanResult = sleepPlanRepository.createPlan(planToCreate)
        if (createdPlanResult is AppResult.Error) {
            return createdPlanResult
        }
        val createdPlan = (createdPlanResult as AppResult.Success).value
        return sleepPlanRepository.setActivePlan(createdUser.id, createdPlan.id)
    }
}
