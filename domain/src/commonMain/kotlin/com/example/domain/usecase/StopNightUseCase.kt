package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Instant

class StopNightUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(endAt: Instant): AppResult<Night> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val activeNightResult = nightRepository.getActiveNight(user.id)
        if (activeNightResult is AppResult.Error) {
            return activeNightResult
        }
        val activeNight = (activeNightResult as AppResult.Success).value
            ?: return AppResult.Error(DomainError.NotFound)
        val durationMinutes = ((endAt.toEpochMilliseconds() - activeNight.startAt.toEpochMilliseconds()) / 60000L)
            .toInt()
            .coerceAtLeast(0)
        val updatedNight = activeNight.copy(endAt = endAt, actualDurationMinutes = durationMinutes)
        val updateResult = nightRepository.updateNight(updatedNight)
        return when (updateResult) {
            is AppResult.Error -> updateResult
            is AppResult.Success -> AppResult.Success(updatedNight)
        }
    }
}
