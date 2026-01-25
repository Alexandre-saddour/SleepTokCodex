package com.example.data.seed

import com.example.data.local.dao.RewardDao
import com.example.data.local.dao.TalentDao
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SeedDataInitializer(
    private val talentDao: TalentDao,
    private val rewardDao: RewardDao,
    private val seedDataProvider: SeedDataProvider,
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun seedIfNeeded(): AppResult<Unit> {
        return try {
            val hasTalents = talentDao.countTalents() > 0
            val hasRewards = rewardDao.countRewards() > 0
            if (hasTalents && hasRewards) {
                return AppResult.Success(Unit)
            }
            val talents = json.decodeFromString<List<SeedTalent>>(seedDataProvider.loadTalentsJson())
            val rewards = json.decodeFromString<List<SeedReward>>(seedDataProvider.loadRewardsJson())
            json.decodeFromString<SeedCoachMessages>(seedDataProvider.loadCoachMessagesJson())
            talentDao.insertAll(talents.map { it.toEntity() })
            rewardDao.insertAll(rewards.map { it.toEntity() })
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
