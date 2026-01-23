package com.example.domain.usecase

import com.example.domain.model.RewardSource
import com.example.domain.model.UserReward
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Clock

class ClaimDailyChestUseCase(
    private val userRepository: UserRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(): AppResult<UserReward> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val rewardsResult = rewardRepository.getRewards()
        if (rewardsResult is AppResult.Error) {
            return rewardsResult
        }
        val rewards = (rewardsResult as AppResult.Success).value
        val userRewardsResult = rewardRepository.getUserRewards(user.id)
        if (userRewardsResult is AppResult.Error) {
            return userRewardsResult
        }
        val ownedIds = (userRewardsResult as AppResult.Success).value.map { it.rewardId }.toSet()
        val rewardToGrant = rewards.firstOrNull { !ownedIds.contains(it.id) } ?: rewards.firstOrNull()
            ?: return AppResult.Error(DomainError.NotFound)
        val userReward = UserReward(
            userId = user.id,
            rewardId = rewardToGrant.id,
            earnedAt = Clock.System.now(),
            source = RewardSource.DAILY_CHEST,
            consumedAt = null,
        )
        val addResult = rewardRepository.addUserReward(userReward)
        return when (addResult) {
            is AppResult.Error -> addResult
            is AppResult.Success -> AppResult.Success(userReward)
        }
    }
}
