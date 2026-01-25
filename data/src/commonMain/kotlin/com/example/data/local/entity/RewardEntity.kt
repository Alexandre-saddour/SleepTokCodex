package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.RewardRarity
import com.example.domain.model.RewardType

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey val id: String,
    val type: RewardType,
    val rarity: RewardRarity,
    val nameKey: String,
    val assetRef: String?,
    val metaJson: String?,
)
