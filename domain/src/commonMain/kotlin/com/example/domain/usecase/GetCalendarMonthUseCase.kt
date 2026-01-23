package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.repository.NightRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import kotlinx.datetime.LocalDate

class GetCalendarMonthUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
) {
    suspend fun execute(startDate: LocalDate, endDate: LocalDate): AppResult<List<Night>> {
        val userResult = userRepository.getActiveUser()
        return when (userResult) {
            is AppResult.Error -> userResult
            is AppResult.Success -> nightRepository.getNightsBetween(userResult.value.id, startDate, endDate)
        }
    }
}
