package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import kotlin.time.Instant

@Entity(
    tableName = "user_talents",
    primaryKeys = ["userId", "talentId"],
    indices = [Index(value = ["talentId"])],
)
data class UserTalentEntity(
    val userId: Long,
    val talentId: String,
    val unlockedAt: Instant,
)
