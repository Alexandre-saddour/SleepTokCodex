package com.example.kmpbackbone.resources

import com.example.data.seed.SeedDataProvider
import kmpbackbone.composeapp.generated.resources.Res

class ResourceSeedDataProvider : SeedDataProvider {
    override suspend fun loadTalentsJson(): String {
        return Res.readBytes("files/seed_talents.json").decodeToString()
    }

    override suspend fun loadRewardsJson(): String {
        return Res.readBytes("files/seed_rewards.json").decodeToString()
    }

    override suspend fun loadCoachMessagesJson(): String {
        return Res.readBytes("files/coach_messages.json").decodeToString()
    }
}
