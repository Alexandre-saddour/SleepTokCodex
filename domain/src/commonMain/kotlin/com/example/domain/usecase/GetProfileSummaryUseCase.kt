package com.example.domain.usecase

import com.example.domain.model.NightStatus
import com.example.domain.model.ProfileSummary
import com.example.domain.repository.NightRepository
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetProfileSummaryUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(timeZone: TimeZone): AppResult<ProfileSummary> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val startDate = user.createdAt.toLocalDateTime(timeZone).date
            val endDate = Clock.System.now().toLocalDateTime(timeZone).date
            val nights = nightRepository.getNightsBetween(user.id, startDate, endDate, timeZone).getOrThrow()
            val rewards = rewardRepository.getUserRewards(user.id).getOrThrow()

            val totalNights = nights.size
            val totalWins = nights.count { it.status == NightStatus.SUCCESS }

            AppResult.Success(
                ProfileSummary(
                    user = user,
                    totalNights = totalNights,
                    totalWins = totalWins,
                    bestStreak = user.streakBest,
                    userRewards = rewards,
                )
            )
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
