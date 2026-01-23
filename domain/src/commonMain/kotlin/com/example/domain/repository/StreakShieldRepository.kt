package com.example.domain.repository

import com.example.domain.model.StreakShield
import com.example.domain.result.AppResult

interface StreakShieldRepository {
    suspend fun getStreakShield(userId: Long): AppResult<StreakShield?>
    suspend fun upsertStreakShield(streakShield: StreakShield): AppResult<Unit>
    suspend fun consumeCharge(userId: Long): AppResult<Unit>
}
