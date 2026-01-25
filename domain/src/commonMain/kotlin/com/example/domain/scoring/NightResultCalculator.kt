package com.example.domain.scoring

import com.example.domain.model.NightResult

interface NightResultCalculator {
    fun calculate(input: NightScoreInput): NightResult
}
