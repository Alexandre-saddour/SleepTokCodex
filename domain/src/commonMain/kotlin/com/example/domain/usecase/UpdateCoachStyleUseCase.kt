package com.example.domain.usecase

import com.example.domain.model.CoachStyle
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class UpdateCoachStyleUseCase(
    private val userRepository: UserRepository,
) {
    suspend fun execute(coachStyle: CoachStyle): AppResult<Unit> {
        return when (val userResult = userRepository.getActiveUser()) {
            is AppResult.Error -> userResult
            is AppResult.Success -> userRepository.updateCoachStyle(userResult.value.id, coachStyle)
        }
    }
}
