package com.example.data.mapper

import com.example.data.local.entity.XpEventEntity
import com.example.domain.model.XpEvent

fun XpEventEntity.toDomain(): XpEvent {
    return XpEvent(
        id = id,
        userId = userId,
        nightId = nightId,
        type = type,
        amount = amount,
        createdAt = createdAt,
        metaJson = metaJson,
    )
}

fun XpEvent.toEntity(): XpEventEntity {
    return XpEventEntity(
        id = id,
        userId = userId,
        nightId = nightId,
        type = type,
        amount = amount,
        createdAt = createdAt,
        metaJson = metaJson,
    )
}
