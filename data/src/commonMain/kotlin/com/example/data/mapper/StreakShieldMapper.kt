package com.example.data.mapper

import com.example.data.local.entity.StreakShieldEntity
import com.example.domain.model.StreakShield

fun StreakShieldEntity.toDomain(): StreakShield {
    return StreakShield(
        userId = userId,
        chargesAvailable = chargesAvailable,
        refreshAt = refreshAt,
        source = source,
    )
}

fun StreakShield.toEntity(): StreakShieldEntity {
    return StreakShieldEntity(
        userId = userId,
        chargesAvailable = chargesAvailable,
        refreshAt = refreshAt,
        source = source,
    )
}
