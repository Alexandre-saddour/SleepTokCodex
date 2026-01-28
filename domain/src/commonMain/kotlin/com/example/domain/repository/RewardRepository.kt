package com.example.domain.repository

import com.example.domain.model.Reward
import com.example.domain.model.UserReward
import com.example.domain.result.AppResult
import kotlin.time.Instant

interface RewardRepository {
    suspend fun getRewards(): AppResult<List<Reward>>
    suspend fun getUserRewards(userId: Long): AppResult<List<UserReward>>
    suspend fun addUserReward(userReward: UserReward): AppResult<Unit>
    suspend fun updateUserRewardConsumedAt(userId: Long, rewardId: String, consumedAt: Instant?): AppResult<Unit>
}
