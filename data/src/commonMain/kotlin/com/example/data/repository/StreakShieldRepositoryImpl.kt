package com.example.data.repository

import com.example.data.local.dao.ShieldDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.StreakShield
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError

class StreakShieldRepositoryImpl(
    private val shieldDao: ShieldDao,
) : StreakShieldRepository {
    override suspend fun getStreakShield(userId: Long): AppResult<StreakShield?> {
        return try {
            val shield = shieldDao.getStreakShield(userId)
            AppResult.Success(shield?.toDomain())
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun upsertStreakShield(streakShield: StreakShield): AppResult<Unit> {
        return try {
            shieldDao.upsert(streakShield.toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun consumeCharge(userId: Long): AppResult<Unit> {
        return try {
            shieldDao.consumeCharge(userId)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
