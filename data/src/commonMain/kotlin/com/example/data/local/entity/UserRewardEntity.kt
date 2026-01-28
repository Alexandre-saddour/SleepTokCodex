package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.example.domain.model.RewardSource
import kotlin.time.Instant

@Entity(
    tableName = "user_rewards",
    primaryKeys = ["userId", "rewardId", "earnedAt"],
    indices = [
        Index(value = ["userId", "rewardId", "earnedAt"], unique = true),
    ],
)
data class UserRewardEntity(
    val userId: Long,
    val rewardId: String,
    val earnedAt: Instant,
    val source: RewardSource,
    val consumedAt: Instant?,
)
