package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.ShieldSource
import kotlin.time.Instant

@Entity(tableName = "streak_shields")
data class StreakShieldEntity(
    @PrimaryKey val userId: Long,
    val chargesAvailable: Int,
    val refreshAt: Instant,
    val source: ShieldSource,
)
