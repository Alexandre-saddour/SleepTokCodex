package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class GetActiveNightUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(): AppResult<Night?> {
        return when (val userResult = userRepository.getActiveUser()) {
            is AppResult.Error -> userResult
            is AppResult.Success -> nightRepository.getActiveNight(userResult.value.id)
        }
    }
}
