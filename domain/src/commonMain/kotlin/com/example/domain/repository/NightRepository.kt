package com.example.domain.repository

import com.example.domain.model.Night
import com.example.domain.result.AppResult
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

interface NightRepository {
    suspend fun getActiveNight(userId: Long): AppResult<Night?>
    suspend fun getNightById(nightId: Long): AppResult<Night?>
    suspend fun getNightsBetween(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone,
    ): AppResult<List<Night>>
    suspend fun createNight(night: Night): AppResult<Night>
    suspend fun updateNight(night: Night): AppResult<Unit>
}
