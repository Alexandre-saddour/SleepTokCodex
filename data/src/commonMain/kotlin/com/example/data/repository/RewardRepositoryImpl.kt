package com.example.data.repository

import com.example.data.local.dao.RewardDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Reward
import com.example.domain.model.UserReward
import com.example.domain.repository.RewardRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlin.time.Instant

class RewardRepositoryImpl(
    private val rewardDao: RewardDao,
) : RewardRepository {
    override suspend fun getRewards(): AppResult<List<Reward>> {
        return try {
            AppResult.Success(rewardDao.getRewards().map { it.toDomain() })
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun getUserRewards(userId: Long): AppResult<List<UserReward>> {
        return try {
            AppResult.Success(rewardDao.getUserRewards(userId).map { it.toDomain() })
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun addUserReward(userReward: UserReward): AppResult<Unit> {
        return try {
            rewardDao.insertUserReward(userReward.toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updateUserRewardConsumedAt(
        userId: Long,
        rewardId: String,
        consumedAt: Instant?,
    ): AppResult<Unit> {
        return try {
            rewardDao.updateUserRewardConsumedAt(userId, rewardId, consumedAt)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
