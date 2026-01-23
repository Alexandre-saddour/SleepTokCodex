package com.example.data.mapper

import com.example.data.local.entity.UserEntity
import com.example.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        createdAt = createdAt,
        timezone = timezone,
        coachStyle = coachStyle,
        premiumStatus = premiumStatus,
        premiumUntil = premiumUntil,
        level = level,
        xpTotal = xpTotal,
        talentPointsAvailable = talentPointsAvailable,
        streakCurrent = streakCurrent,
        streakBest = streakBest,
        lastNightId = lastNightId,
        baselineSleepDurationMinutes = baselineSleepDurationMinutes,
        settingsJson = settingsJson,
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        createdAt = createdAt,
        timezone = timezone,
        coachStyle = coachStyle,
        premiumStatus = premiumStatus,
        premiumUntil = premiumUntil,
        level = level,
        xpTotal = xpTotal,
        talentPointsAvailable = talentPointsAvailable,
        streakCurrent = streakCurrent,
        streakBest = streakBest,
        lastNightId = lastNightId,
        baselineSleepDurationMinutes = baselineSleepDurationMinutes,
        settingsJson = settingsJson,
    )
}
