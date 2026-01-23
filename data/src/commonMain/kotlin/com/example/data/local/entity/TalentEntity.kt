package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.TalentBranch
import com.example.domain.model.TalentTier

@Entity(
    tableName = "talents",
    indices = [
        Index(value = ["branch", "tier"]),
    ],
)
data class TalentEntity(
    @PrimaryKey val id: String,
    val branch: TalentBranch,
    val tier: TalentTier,
    val nameKey: String,
    val descriptionKey: String,
    val costPoints: Int,
    val effectJson: String,
    val isActive: Boolean,
)
