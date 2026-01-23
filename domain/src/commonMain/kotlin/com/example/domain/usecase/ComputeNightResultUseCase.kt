package com.example.domain.usecase

import com.example.domain.model.NightResult
import com.example.domain.scoring.NightResultCalculator
import com.example.domain.scoring.NightScoreInput
import com.example.domain.result.AppResult

class ComputeNightResultUseCase(
    private val calculator: NightResultCalculator,
) {
    suspend fun execute(input: NightScoreInput): AppResult<NightResult> {
        return AppResult.Success(calculator.calculate(input))
    }
}
