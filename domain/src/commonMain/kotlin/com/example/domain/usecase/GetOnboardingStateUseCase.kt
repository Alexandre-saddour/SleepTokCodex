package com.example.domain.usecase

import com.example.domain.model.OnboardingState
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError

class GetOnboardingStateUseCase(
    private val userRepository: UserRepository,
    private val sleepPlanRepository: SleepPlanRepository,
) {
    suspend fun execute(): AppResult<OnboardingState> {
        return when (val userResult = userRepository.getActiveUser()) {
            is AppResult.Error -> {
                if (userResult.error == DomainError.NotFound) {
                    AppResult.Success(OnboardingState(isComplete = false, user = null, plan = null))
                } else {
                    userResult
                }
            }
            is AppResult.Success -> {
                val user = userResult.value
                when (val planResult = sleepPlanRepository.getActivePlan(user.id)) {
                    is AppResult.Error -> {
                        if (planResult.error == DomainError.NotFound) {
                            AppResult.Success(OnboardingState(isComplete = false, user = user, plan = null))
                        } else {
                            planResult
                        }
                    }
                    is AppResult.Success -> {
                        val plan = planResult.value
                        AppResult.Success(OnboardingState(isComplete = plan != null, user = user, plan = plan))
                    }
                }
            }
        }
    }
}
