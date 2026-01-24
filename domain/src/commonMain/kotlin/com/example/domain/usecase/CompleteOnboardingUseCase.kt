package com.example.domain.usecase

import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.TransactionRunner
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class CompleteOnboardingUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val transactionRunner: TransactionRunner,
) {
    private class RollbackException(val error: AppResult.Error) : Exception()

    suspend fun execute(user: User, plan: SleepPlan): AppResult<Unit> {
        return try {
            transactionRunner.run {
                val createdUserResult = userRepository.createUser(user)
                if (createdUserResult is AppResult.Error) {
                    throw RollbackException(createdUserResult)
                }
                val createdUser = (createdUserResult as AppResult.Success).value
                val planToCreate = plan.copy(userId = createdUser.id)
                val createdPlanResult = sleepPlanRepository.createPlan(planToCreate)
                if (createdPlanResult is AppResult.Error) {
                    throw RollbackException(createdPlanResult)
                }
                val createdPlan = (createdPlanResult as AppResult.Success).value
                val activateResult = sleepPlanRepository.setActivePlan(createdUser.id, createdPlan.id)
                if (activateResult is AppResult.Error) {
                    throw RollbackException(activateResult)
                }
                AppResult.Success(Unit)
            }
        } catch (e: RollbackException) {
            e.error
        }
    }
}
