package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

@Entity(
    tableName = "sleep_plans",
    indices = [Index("userId")],
)
data class SleepPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val planStartLocalTime: LocalTime,
    val planEndLocalTime: LocalTime,
    val activeDaysMask: Int,
    val toleranceStartMinutes: Int,
    val toleranceEndMinutes: Int,
    val createdAt: Instant,
    val isActive: Boolean,
)
