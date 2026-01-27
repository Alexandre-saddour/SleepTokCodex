package com.example.domain.usecase

import com.example.domain.model.BadgesAndCosmetics
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow

class GetBadgesAndCosmeticsUseCase(
    private val userRepository: UserRepository,
    private val rewardRepository: RewardRepository,
) {
    suspend fun execute(): AppResult<BadgesAndCosmetics> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val rewards = rewardRepository.getRewards().getOrThrow()
            val userRewards = rewardRepository.getUserRewards(user.id).getOrThrow()

            AppResult.Success(
                BadgesAndCosmetics(
                    rewards = rewards,
                    userRewards = userRewards,
                )
            )
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
