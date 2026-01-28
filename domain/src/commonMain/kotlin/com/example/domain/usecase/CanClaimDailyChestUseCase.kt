package com.example.domain.usecase

import com.example.domain.model.RewardSource
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import com.example.domain.util.toLocalDateTime
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

class CanClaimDailyChestUseCase(
    private val userRepository: UserRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(timeZone: TimeZone): AppResult<Boolean> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val userRewards = rewardRepository.getUserRewards(user.id).getOrThrow()

            val now = Clock.System.now()
            val today = now.toLocalDateTime(timeZone).date

            val dailyChestRewards = userRewards.filter { it.source == RewardSource.DAILY_CHEST }
            val claimedToday = dailyChestRewards.any { reward ->
                val claimDate = reward.earnedAt.toLocalDateTime(timeZone).date
                claimDate == today
            }

            AppResult.Success(!claimedToday)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
