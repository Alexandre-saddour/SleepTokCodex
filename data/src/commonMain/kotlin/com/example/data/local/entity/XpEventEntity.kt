package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.XpEventType
import kotlin.time.Instant

@Entity(
    tableName = "xp_events",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["nightId"]),
    ],
)
data class XpEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val nightId: Long?,
    val type: XpEventType,
    val amount: Int,
    val createdAt: Instant,
    val metaJson: String?,
)
