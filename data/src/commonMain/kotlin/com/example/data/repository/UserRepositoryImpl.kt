package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.CoachStyle
import com.example.domain.model.PremiumStatus
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Instant

class UserRepositoryImpl(
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun getActiveUser(): AppResult<User> {
        return try {
            val user = userDao.getActiveUser() ?: return AppResult.Error(DomainError.NotFound)
            AppResult.Success(user.toDomain())
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun createUser(user: User): AppResult<User> {
        return try {
            val id = userDao.insert(user.toEntity())
            AppResult.Success(user.copy(id = id))
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updateUser(user: User): AppResult<Unit> {
        return try {
            userDao.update(user.toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updateCoachStyle(userId: Long, coachStyle: CoachStyle): AppResult<Unit> {
        return try {
            userDao.updateCoachStyle(userId, coachStyle)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updatePremiumStatus(
        userId: Long,
        status: PremiumStatus,
        premiumUntil: Instant?,
    ): AppResult<Unit> {
        return try {
            userDao.updatePremiumStatus(userId, status, premiumUntil)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updateStreak(userId: Long, current: Int, best: Int): AppResult<Unit> {
        return try {
            userDao.updateStreak(userId, current, best)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updateXp(
        userId: Long,
        xpTotal: Long,
        level: Int,
        talentPointsAvailable: Int,
    ): AppResult<Unit> {
        return try {
            userDao.updateXp(userId, xpTotal, level, talentPointsAvailable)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
