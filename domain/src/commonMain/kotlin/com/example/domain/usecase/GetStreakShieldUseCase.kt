package com.example.domain.usecase

import com.example.domain.model.StreakShield
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow

class GetStreakShieldUseCase(
    private val userRepository: UserRepository,
    private val streakShieldRepository: StreakShieldRepository,
) {
    suspend fun execute(): AppResult<StreakShield?> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            streakShieldRepository.getStreakShield(user.id)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
