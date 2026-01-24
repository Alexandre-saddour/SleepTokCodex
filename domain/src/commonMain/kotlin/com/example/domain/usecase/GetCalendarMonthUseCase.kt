package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

class GetCalendarMonthUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(
        startDate: LocalDate,
        endDate: LocalDate,
        timeZone: TimeZone,
    ): AppResult<List<Night>> {
        val userResult = userRepository.getActiveUser()
        return when (userResult) {
            is AppResult.Error -> userResult
            is AppResult.Success -> nightRepository.getNightsBetween(
                userResult.value.id,
                startDate,
                endDate,
                timeZone,
            )
        }
    }
}
