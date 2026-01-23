package com.example.domain.model

import kotlinx.datetime.Instant

data class XpEvent(
    val id: Long,
    val userId: Long,
    val nightId: Long?,
    val type: XpEventType,
    val amount: Int,
    val createdAt: Instant,
    val metaJson: String?,
)
