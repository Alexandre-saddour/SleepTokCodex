package com.example.domain.usecase

import com.example.domain.model.StreakShield
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class GetStreakShieldUseCase(
    private val userRepository: UserRepository,
    private val streakShieldRepository: StreakShieldRepository,
) {
    suspend fun execute(): AppResult<StreakShield?> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        return streakShieldRepository.getStreakShield(user.id)
    }
}
