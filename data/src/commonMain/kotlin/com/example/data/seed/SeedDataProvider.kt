package com.example.data.seed

interface SeedDataProvider {
    suspend fun loadTalentsJson(): String
    suspend fun loadRewardsJson(): String
    suspend fun loadCoachMessagesJson(): String
}
