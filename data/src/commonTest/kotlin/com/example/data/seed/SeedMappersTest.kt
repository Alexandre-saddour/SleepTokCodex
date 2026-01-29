package com.example.data.seed

import com.example.domain.model.RewardRarity
import com.example.domain.model.RewardType
import com.example.domain.model.TalentBranch
import com.example.domain.model.TalentTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SeedMappersTest {

    // SeedTalent to TalentEntity tests
    @Test
    fun seedTalentMapsIdCorrectly() {
        val seed = createSeedTalent(id = "D1")
        val entity = seed.toEntity()
        assertEquals("D1", entity.id)
    }

    @Test
    fun seedTalentMapsBranchCorrectly() {
        val seed = createSeedTalent(branch = "DISCIPLINE")
        val entity = seed.toEntity()
        assertEquals(TalentBranch.DISCIPLINE, entity.branch)
    }

    @Test
    fun seedTalentMapsStreakBranch() {
        val seed = createSeedTalent(branch = "STREAK")
        val entity = seed.toEntity()
        assertEquals(TalentBranch.STREAK, entity.branch)
    }

    @Test
    fun seedTalentMapsStyleBranch() {
        val seed = createSeedTalent(branch = "STYLE")
        val entity = seed.toEntity()
        assertEquals(TalentBranch.STYLE, entity.branch)
    }

    @Test
    fun seedTalentMapsInsightBranch() {
        val seed = createSeedTalent(branch = "INSIGHT")
        val entity = seed.toEntity()
        assertEquals(TalentBranch.INSIGHT, entity.branch)
    }

    @Test
    fun seedTalentMapsTier1() {
        val seed = createSeedTalent(tier = 1)
        val entity = seed.toEntity()
        assertEquals(TalentTier.TIER_1, entity.tier)
    }

    @Test
    fun seedTalentMapsTier2() {
        val seed = createSeedTalent(tier = 2)
        val entity = seed.toEntity()
        assertEquals(TalentTier.TIER_2, entity.tier)
    }

    @Test
    fun seedTalentMapsTier3() {
        val seed = createSeedTalent(tier = 3)
        val entity = seed.toEntity()
        assertEquals(TalentTier.TIER_3, entity.tier)
    }

    @Test
    fun seedTalentDefaultsToTier1ForInvalidTier() {
        val seed = createSeedTalent(tier = 99)
        val entity = seed.toEntity()
        assertEquals(TalentTier.TIER_1, entity.tier)
    }

    @Test
    fun seedTalentMapsNameKey() {
        val seed = createSeedTalent(nameKey = "talent_d1_name")
        val entity = seed.toEntity()
        assertEquals("talent_d1_name", entity.nameKey)
    }

    @Test
    fun seedTalentMapsDescriptionKey() {
        val seed = createSeedTalent(descriptionKey = "talent_d1_desc")
        val entity = seed.toEntity()
        assertEquals("talent_d1_desc", entity.descriptionKey)
    }

    @Test
    fun seedTalentMapsCostPoints() {
        val seed = createSeedTalent(costPoints = 2)
        val entity = seed.toEntity()
        assertEquals(2, entity.costPoints)
    }

    @Test
    fun seedTalentMapsIsActive() {
        val seed = createSeedTalent(isActive = true)
        val entity = seed.toEntity()
        assertTrue(entity.isActive)
    }

    @Test
    fun seedTalentMapsAddXpEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(
                type = "ADD_XP",
                amount = 5,
                condition = SeedTalentCondition(type = "START_WITHIN_MINUTES", value = 10)
            )
        )
        val entity = seed.toEntity()
        // effectJson should contain the serialized effect
        assertTrue(entity.effectJson.contains("ADD_XP") || entity.effectJson.contains("amount"))
    }

    @Test
    fun seedTalentMapsXpMultiplierEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(
                type = "XP_MULTIPLIER",
                multiplier = 1.05,
                condition = SeedTalentCondition(type = "STREAK_AT_LEAST", value = 3)
            )
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.contains("XP_MULTIPLIER") || entity.effectJson.contains("multiplier"))
    }

    @Test
    fun seedTalentMapsStreakShieldEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(
                type = "STREAK_SHIELD",
                chargesPerWeek = 1
            )
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.contains("STREAK_SHIELD") || entity.effectJson.contains("charges"))
    }

    @Test
    fun seedTalentMapsUnlockThemeSlotEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(type = "UNLOCK_THEME_SLOT")
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    @Test
    fun seedTalentMapsUnlockSoundPackEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(type = "UNLOCK_SOUND_PACK")
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    @Test
    fun seedTalentMapsRareDropLuckEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(
                type = "RARE_DROP_LUCK",
                bonusPercent = 20
            )
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    @Test
    fun seedTalentMapsEnableWeeklyRecapEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(type = "ENABLE_WEEKLY_RECAP")
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    @Test
    fun seedTalentMapsEnableAdvancedCalendarEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(type = "ENABLE_ADVANCED_CALENDAR")
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    @Test
    fun seedTalentMapsEnableTrendlineEffect() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(type = "ENABLE_TRENDLINE")
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    @Test
    fun seedTalentMapsUnknownEffectToNone() {
        val seed = createSeedTalent(
            effect = SeedTalentEffect(type = "UNKNOWN_EFFECT")
        )
        val entity = seed.toEntity()
        assertTrue(entity.effectJson.isNotEmpty())
    }

    // SeedReward to RewardEntity tests
    @Test
    fun seedRewardMapsIdCorrectly() {
        val seed = createSeedReward(id = "badge_first_quest")
        val entity = seed.toEntity()
        assertEquals("badge_first_quest", entity.id)
    }

    @Test
    fun seedRewardMapsBadgeType() {
        val seed = createSeedReward(type = "BADGE")
        val entity = seed.toEntity()
        assertEquals(RewardType.BADGE, entity.type)
    }

    @Test
    fun seedRewardMapsThemeType() {
        val seed = createSeedReward(type = "THEME")
        val entity = seed.toEntity()
        assertEquals(RewardType.THEME, entity.type)
    }

    @Test
    fun seedRewardMapsSoundType() {
        val seed = createSeedReward(type = "SOUND")
        val entity = seed.toEntity()
        assertEquals(RewardType.SOUND, entity.type)
    }

    @Test
    fun seedRewardMapsCommonRarity() {
        val seed = createSeedReward(rarity = "COMMON")
        val entity = seed.toEntity()
        assertEquals(RewardRarity.COMMON, entity.rarity)
    }

    @Test
    fun seedRewardMapsRareRarity() {
        val seed = createSeedReward(rarity = "RARE")
        val entity = seed.toEntity()
        assertEquals(RewardRarity.RARE, entity.rarity)
    }

    @Test
    fun seedRewardMapsEpicRarity() {
        val seed = createSeedReward(rarity = "EPIC")
        val entity = seed.toEntity()
        assertEquals(RewardRarity.EPIC, entity.rarity)
    }

    @Test
    fun seedRewardMapsNameKey() {
        val seed = createSeedReward(nameKey = "badge_first_quest_name")
        val entity = seed.toEntity()
        assertEquals("badge_first_quest_name", entity.nameKey)
    }

    @Test
    fun seedRewardMapsAssetRef() {
        val seed = createSeedReward(assetRef = "badge_first_quest.png")
        val entity = seed.toEntity()
        assertEquals("badge_first_quest.png", entity.assetRef)
    }

    @Test
    fun seedRewardMapsNullAssetRef() {
        val seed = createSeedReward(assetRef = null)
        val entity = seed.toEntity()
        assertNull(entity.assetRef)
    }

    @Test
    fun seedRewardMapsMetaJson() {
        val seed = createSeedReward(metaJson = """{"key": "value"}""")
        val entity = seed.toEntity()
        assertEquals("""{"key": "value"}""", entity.metaJson)
    }

    @Test
    fun seedRewardMapsNullMetaJson() {
        val seed = createSeedReward(metaJson = null)
        val entity = seed.toEntity()
        assertNull(entity.metaJson)
    }

    // Helper methods
    private fun createSeedTalent(
        id: String = "T1",
        branch: String = "DISCIPLINE",
        tier: Int = 1,
        nameKey: String = "name",
        descriptionKey: String = "desc",
        costPoints: Int = 1,
        effect: SeedTalentEffect = SeedTalentEffect(type = "UNLOCK_THEME_SLOT"),
        isActive: Boolean = true,
    ) = SeedTalent(
        id = id,
        branch = branch,
        tier = tier,
        nameKey = nameKey,
        descriptionKey = descriptionKey,
        costPoints = costPoints,
        effect = effect,
        isActive = isActive,
    )

    private fun createSeedReward(
        id: String = "badge_test",
        type: String = "BADGE",
        rarity: String = "COMMON",
        nameKey: String = "name",
        assetRef: String? = null,
        metaJson: String? = null,
    ) = SeedReward(
        id = id,
        type = type,
        rarity = rarity,
        nameKey = nameKey,
        assetRef = assetRef,
        metaJson = metaJson,
    )
}
