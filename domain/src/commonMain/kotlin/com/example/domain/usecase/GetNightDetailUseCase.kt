package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.result.AppResult

class GetNightDetailUseCase(
    private val nightRepository: NightRepository,
) {
    suspend fun execute(nightId: Long): AppResult<Night?> {
        return nightRepository.getNightById(nightId)
    }
}
