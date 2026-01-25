package com.example.domain.usecase

import com.example.domain.model.BadgesAndCosmetics
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class GetBadgesAndCosmeticsUseCase(
    private val userRepository: UserRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(): AppResult<BadgesAndCosmetics> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val rewardsResult = rewardRepository.getRewards()
        if (rewardsResult is AppResult.Error) {
            return rewardsResult
        }
        val userRewardsResult = rewardRepository.getUserRewards(user.id)
        if (userRewardsResult is AppResult.Error) {
            return userRewardsResult
        }
        return AppResult.Success(
            BadgesAndCosmetics(
                rewards = (rewardsResult as AppResult.Success).value,
                userRewards = (userRewardsResult as AppResult.Success).value,
            )
        )
    }
}
