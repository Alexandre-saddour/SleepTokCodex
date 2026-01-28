package com.example.domain.model

import kotlin.time.Instant

data class Reward(
    val id: String,
    val type: RewardType,
    val rarity: RewardRarity,
    val nameKey: String,
    val assetRef: String?,
    val metaJson: String?,
)

data class UserReward(
    val userId: Long,
    val rewardId: String,
    val earnedAt: Instant,
    val source: RewardSource,
    val consumedAt: Instant?,
)
