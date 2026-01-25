package com.example.data.mapper

import com.example.data.local.entity.NightEntity
import com.example.domain.model.Night

fun NightEntity.toDomain(): Night {
    return Night(
        id = id,
        userId = userId,
        planId = planId,
        startAt = startAt,
        endAt = endAt,
        status = status,
        actualDurationMinutes = actualDurationMinutes,
        planDurationMinutes = planDurationMinutes,
        score = score,
        xpEarned = xpEarned,
        streakBefore = streakBefore,
        streakAfter = streakAfter,
        createdAt = createdAt,
        note = note,
    )
}

fun Night.toEntity(): NightEntity {
    return NightEntity(
        id = id,
        userId = userId,
        planId = planId,
        startAt = startAt,
        endAt = endAt,
        status = status,
        actualDurationMinutes = actualDurationMinutes,
        planDurationMinutes = planDurationMinutes,
        score = score,
        xpEarned = xpEarned,
        streakBefore = streakBefore,
        streakAfter = streakAfter,
        createdAt = createdAt,
        note = note,
    )
}
