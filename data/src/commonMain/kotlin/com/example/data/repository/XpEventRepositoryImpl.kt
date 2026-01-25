package com.example.data.repository

import com.example.data.local.dao.XpEventDao
import com.example.data.mapper.toEntity
import com.example.domain.model.XpEvent
import com.example.domain.repository.XpEventRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError

class XpEventRepositoryImpl(
    private val xpEventDao: XpEventDao,
) : XpEventRepository {
    override suspend fun addXpEvent(event: XpEvent): AppResult<Unit> {
        return try {
            xpEventDao.insert(event.toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun addXpEvents(events: List<XpEvent>): AppResult<Unit> {
        return try {
            xpEventDao.insertAll(events.map { it.toEntity() })
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
