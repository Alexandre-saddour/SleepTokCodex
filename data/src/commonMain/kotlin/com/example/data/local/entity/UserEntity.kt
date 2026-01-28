package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.CoachStyle
import com.example.domain.model.PremiumStatus
import kotlin.time.Instant

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Instant,
    val timezone: String,
    val coachStyle: CoachStyle,
    val premiumStatus: PremiumStatus,
    val premiumUntil: Instant?,
    val level: Int,
    val xpTotal: Long,
    val talentPointsAvailable: Int,
    val streakCurrent: Int,
    val streakBest: Int,
    val lastNightId: Long?,
    val baselineSleepDurationMinutes: Int,
    val settingsJson: String?,
)
