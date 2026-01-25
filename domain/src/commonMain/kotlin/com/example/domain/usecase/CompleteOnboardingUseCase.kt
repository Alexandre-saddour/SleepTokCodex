package com.example.domain.usecase

import com.example.domain.model.RollbackException
import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.model.getOrRollback
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.TransactionRunner
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class CompleteOnboardingUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
    private val transactionRunner: TransactionRunner,
) {
    suspend fun execute(user: User, plan: SleepPlan): AppResult<Unit> {
        return try {
            transactionRunner.run {
                val createdUser = userRepository.createUser(user).getOrRollback()
                val planToCreate = plan.copy(userId = createdUser.id)
                val createdPlan = sleepPlanRepository.createPlan(planToCreate).getOrRollback()
                sleepPlanRepository.setActivePlan(createdUser.id, createdPlan.id).getOrRollback()
                AppResult.Success(Unit)
            }
        } catch (e: RollbackException) {
            e.error
        }
    }
}
