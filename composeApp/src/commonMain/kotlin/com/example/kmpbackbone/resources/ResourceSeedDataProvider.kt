package com.example.kmpbackbone.resources

import com.example.data.seed.SeedDataProvider
import org.jetbrains.compose.resources.readResource

class ResourceSeedDataProvider : SeedDataProvider {
    override suspend fun loadTalentsJson(): String {
        return readResource("files/seed_talents.json").decodeToString()
    }

    override suspend fun loadRewardsJson(): String {
        return readResource("files/seed_rewards.json").decodeToString()
    }

    override suspend fun loadCoachMessagesJson(): String {
        return readResource("files/coach_messages.json").decodeToString()
    }
}
