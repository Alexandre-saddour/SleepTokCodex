package com.example.data.mapper

import com.example.data.local.entity.SleepPlanEntity
import com.example.domain.model.SleepPlan

fun SleepPlanEntity.toDomain(): SleepPlan {
    return SleepPlan(
        id = id,
        userId = userId,
        planStartLocalTime = planStartLocalTime,
        planEndLocalTime = planEndLocalTime,
        activeDaysMask = activeDaysMask,
        toleranceStartMinutes = toleranceStartMinutes,
        toleranceEndMinutes = toleranceEndMinutes,
        createdAt = createdAt,
        isActive = isActive,
    )
}

fun SleepPlan.toEntity(): SleepPlanEntity {
    return SleepPlanEntity(
        id = id,
        userId = userId,
        planStartLocalTime = planStartLocalTime,
        planEndLocalTime = planEndLocalTime,
        activeDaysMask = activeDaysMask,
        toleranceStartMinutes = toleranceStartMinutes,
        toleranceEndMinutes = toleranceEndMinutes,
        createdAt = createdAt,
        isActive = isActive,
    )
}
