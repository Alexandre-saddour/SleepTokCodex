package com.example.domain.usecase

import com.example.domain.model.NightStatus
import com.example.domain.model.ProfileSummary
import com.example.domain.repository.NightRepository
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetProfileSummaryUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(timeZone: TimeZone): AppResult<ProfileSummary> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val startDate = user.createdAt.toLocalDateTime(timeZone).date
        val endDate = Clock.System.todayIn(timeZone)
        val nightsResult = nightRepository.getNightsBetween(user.id, startDate, endDate)
        if (nightsResult is AppResult.Error) {
            return nightsResult
        }
        val nights = (nightsResult as AppResult.Success).value
        val rewardsResult = rewardRepository.getUserRewards(user.id)
        if (rewardsResult is AppResult.Error) {
            return rewardsResult
        }
        val rewards = (rewardsResult as AppResult.Success).value
        val totalNights = nights.size
        val totalWins = nights.count { it.status == NightStatus.SUCCESS }
        return AppResult.Success(
            ProfileSummary(
                user = user,
                totalNights = totalNights,
                totalWins = totalWins,
                bestStreak = user.streakBest,
                userRewards = rewards,
            )
        )
    }
}
