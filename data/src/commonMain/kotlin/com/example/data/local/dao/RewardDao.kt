package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.UserRewardEntity
import kotlin.time.Instant

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards")
    suspend fun getRewards(): List<RewardEntity>

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun countRewards(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<RewardEntity>)

    @Query("SELECT * FROM user_rewards WHERE userId = :userId")
    suspend fun getUserRewards(userId: Long): List<UserRewardEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserReward(userReward: UserRewardEntity)

    @Query("UPDATE user_rewards SET consumedAt = :consumedAt WHERE userId = :userId AND rewardId = :rewardId")
    suspend fun updateUserRewardConsumedAt(userId: Long, rewardId: String, consumedAt: Instant?)
}
