package com.example.domain.repository

import com.example.domain.model.XpEvent
import com.example.domain.result.AppResult

interface XpEventRepository {
    suspend fun addXpEvent(event: XpEvent): AppResult<Unit>
    suspend fun addXpEvents(events: List<XpEvent>): AppResult<Unit>
}
