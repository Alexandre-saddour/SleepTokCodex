package com.example.domain.repository

import com.example.domain.model.CoachStyle
import com.example.domain.model.PremiumStatus
import com.example.domain.model.User
import kotlinx.datetime.Instant
import com.example.domain.result.AppResult

interface UserRepository {
    suspend fun getActiveUser(): AppResult<User>
    suspend fun createUser(user: User): AppResult<User>
    suspend fun updateUser(user: User): AppResult<Unit>
    suspend fun updateCoachStyle(userId: Long, coachStyle: CoachStyle): AppResult<Unit>
    suspend fun updatePremiumStatus(userId: Long, status: PremiumStatus, premiumUntil: Instant?): AppResult<Unit>
    suspend fun updateStreak(userId: Long, current: Int, best: Int): AppResult<Unit>
    suspend fun updateXp(userId: Long, xpTotal: Long, level: Int, talentPointsAvailable: Int): AppResult<Unit>
}
