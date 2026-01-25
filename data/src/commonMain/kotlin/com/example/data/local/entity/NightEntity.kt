package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.NightStatus
import kotlinx.datetime.Instant

@Entity(
    tableName = "nights",
    indices = [
        Index(value = ["userId", "startAt"]),
        Index(value = ["planId"]),
    ],
)
data class NightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val planId: Long,
    val startAt: Instant,
    val endAt: Instant?,
    val status: NightStatus,
    val actualDurationMinutes: Int?,
    val planDurationMinutes: Int,
    val score: Int?,
    val xpEarned: Int?,
    val streakBefore: Int?,
    val streakAfter: Int?,
    val createdAt: Instant,
    val note: String?,
)
