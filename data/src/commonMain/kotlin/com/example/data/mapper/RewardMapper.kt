package com.example.data.mapper

import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.UserRewardEntity
import com.example.domain.model.Reward
import com.example.domain.model.UserReward

fun RewardEntity.toDomain(): Reward {
    return Reward(
        id = id,
        type = type,
        rarity = rarity,
        nameKey = nameKey,
        assetRef = assetRef,
        metaJson = metaJson,
    )
}

fun Reward.toEntity(): RewardEntity {
    return RewardEntity(
        id = id,
        type = type,
        rarity = rarity,
        nameKey = nameKey,
        assetRef = assetRef,
        metaJson = metaJson,
    )
}

fun UserRewardEntity.toDomain(): UserReward {
    return UserReward(
        userId = userId,
        rewardId = rewardId,
        earnedAt = earnedAt,
        source = source,
        consumedAt = consumedAt,
    )
}

fun UserReward.toEntity(): UserRewardEntity {
    return UserRewardEntity(
        userId = userId,
        rewardId = rewardId,
        earnedAt = earnedAt,
        source = source,
        consumedAt = consumedAt,
    )
}
