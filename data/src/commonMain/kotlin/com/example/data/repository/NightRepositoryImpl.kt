package com.example.data.repository

import com.example.data.local.dao.NightDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Instant as KxInstant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class NightRepositoryImpl(
    private val nightDao: NightDao,
) : NightRepository {
    override suspend fun getActiveNight(userId: Long): AppResult<Night?> {
        return try {
            val night = nightDao.getActiveNight(userId)
            AppResult.Success(night?.toDomain())
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun getNightById(nightId: Long): AppResult<Night?> {
        return try {
            val night = nightDao.getNightById(nightId)
            AppResult.Success(night?.toDomain())
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun getNightsBetween(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): AppResult<List<Night>> {
        return try {
            val timeZone = TimeZone.currentSystemDefault()
            val startInstant = LocalDateTime(startDate, LocalTime(hour = 0, minute = 0)).toInstant(timeZone)
            val endInstant = LocalDateTime(endDate, LocalTime(hour = 23, minute = 59, second = 59)).toInstant(timeZone)
            val nights = nightDao.getNightsBetween(
                userId,
                KxInstant.fromEpochMilliseconds(startInstant.toEpochMilliseconds()),
                KxInstant.fromEpochMilliseconds(endInstant.toEpochMilliseconds()),
            )
            AppResult.Success(nights.map { it.toDomain() })
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun createNight(night: Night): AppResult<Night> {
        return try {
            val id = nightDao.insert(night.toEntity())
            AppResult.Success(night.copy(id = id))
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updateNight(night: Night): AppResult<Unit> {
        return try {
            nightDao.update(night.toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
