package com.example.data.seed

import kotlinx.serialization.Serializable

@Serializable
data class SeedTalent(
    val id: String,
    val branch: String,
    val tier: Int,
    val nameKey: String,
    val descriptionKey: String,
    val costPoints: Int,
    val effect: SeedTalentEffect,
    val isActive: Boolean = true,
)

@Serializable
data class SeedTalentEffect(
    val type: String,
    val amount: Int? = null,
    val multiplier: Double? = null,
    val chargesPerWeek: Int? = null,
    val bonusPercent: Int? = null,
    val condition: SeedTalentCondition? = null,
)

@Serializable
data class SeedTalentCondition(
    val type: String,
    val value: Int? = null,
)

@Serializable
data class SeedReward(
    val id: String,
    val type: String,
    val rarity: String,
    val nameKey: String,
    val assetRef: String? = null,
    val metaJson: String? = null,
)

@Serializable
data class SeedCoachMessages(
    val success: SeedCoachGroup,
    val partial: SeedCoachGroup,
    val fail: SeedCoachGroup,
    val recap: SeedRecapGroup,
)

@Serializable
data class SeedCoachGroup(
    val chill: List<String>,
    val hype: List<String>,
    val strict: List<String>,
    val factual: List<String>,
    val tips: List<String>,
)

@Serializable
data class SeedRecapGroup(
    val highlight: List<String>,
    val objective: List<String>,
)
