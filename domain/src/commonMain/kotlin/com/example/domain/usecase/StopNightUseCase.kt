package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import kotlinx.datetime.Instant

class StopNightUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(endAt: Instant): AppResult<Night> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val activeNight = nightRepository.getActiveNight(user.id).getOrThrow()
                ?: return AppResult.Error(DomainError.NotFound)

            val durationMinutes = ((endAt.toEpochMilliseconds() - activeNight.startAt.toEpochMilliseconds()) / 60000L)
                .toInt()
                .coerceAtLeast(0)
            val updatedNight = activeNight.copy(endAt = endAt, actualDurationMinutes = durationMinutes)

            nightRepository.updateNight(updatedNight).getOrThrow()
            AppResult.Success(updatedNight)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
