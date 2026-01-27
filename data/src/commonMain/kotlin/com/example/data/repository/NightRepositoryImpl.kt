package com.example.data.repository

import com.example.data.local.dao.NightDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Instant
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
        timeZone: TimeZone,
    ): AppResult<List<Night>> {
        return try {
            val startInstant = LocalDateTime(startDate, LocalTime(hour = 0, minute = 0))
                .toInstant(timeZone)
                .toKotlinxInstant()
            val endInstant = LocalDateTime(endDate, LocalTime(hour = 23, minute = 59, second = 59))
                .toInstant(timeZone)
                .toKotlinxInstant()
            val nights = nightDao.getNightsBetween(userId, startInstant, endInstant)
            AppResult.Success(nights.map { it.toDomain() })
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    private fun kotlin.time.Instant.toKotlinxInstant(): Instant {
        return Instant.fromEpochMilliseconds(toEpochMilliseconds())
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
