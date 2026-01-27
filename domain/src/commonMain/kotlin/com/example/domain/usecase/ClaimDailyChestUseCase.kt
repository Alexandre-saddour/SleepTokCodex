package com.example.domain.usecase

import com.example.domain.model.RewardSource
import com.example.domain.model.UserReward
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import kotlinx.datetime.Clock

class ClaimDailyChestUseCase(
    private val userRepository: UserRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(): AppResult<UserReward> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val rewards = rewardRepository.getRewards().getOrThrow()
            val userRewards = rewardRepository.getUserRewards(user.id).getOrThrow()

            val ownedIds = userRewards.map { it.rewardId }.toSet()
            val rewardToGrant = rewards.firstOrNull { !ownedIds.contains(it.id) }
                ?: rewards.firstOrNull()
                ?: return AppResult.Error(DomainError.NotFound)

            val userReward = UserReward(
                userId = user.id,
                rewardId = rewardToGrant.id,
                earnedAt = Clock.System.now(),
                source = RewardSource.DAILY_CHEST,
                consumedAt = null,
            )

            rewardRepository.addUserReward(userReward).getOrThrow()
            AppResult.Success(userReward)
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
