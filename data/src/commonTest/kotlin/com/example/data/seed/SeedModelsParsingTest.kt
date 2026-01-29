package com.example.data.seed

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SeedModelsParsingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parseSeedTalentWithAddXpEffect() {
        val jsonString = """
            {
                "id": "D1",
                "branch": "DISCIPLINE",
                "tier": 1,
                "nameKey": "talent_d1_name",
                "descriptionKey": "talent_d1_desc",
                "costPoints": 1,
                "effect": {
                    "type": "ADD_XP",
                    "amount": 5,
                    "condition": {
                        "type": "START_WITHIN_MINUTES",
                        "value": 10
                    }
                },
                "isActive": true
            }
        """.trimIndent()

        val talent = json.decodeFromString<SeedTalent>(jsonString)

        assertEquals("D1", talent.id)
        assertEquals("DISCIPLINE", talent.branch)
        assertEquals(1, talent.tier)
        assertEquals("talent_d1_name", talent.nameKey)
        assertEquals("talent_d1_desc", talent.descriptionKey)
        assertEquals(1, talent.costPoints)
        assertEquals("ADD_XP", talent.effect.type)
        assertEquals(5, talent.effect.amount)
        assertNotNull(talent.effect.condition)
        assertEquals("START_WITHIN_MINUTES", talent.effect.condition!!.type)
        assertEquals(10, talent.effect.condition!!.value)
        assertTrue(talent.isActive)
    }

    @Test
    fun parseSeedTalentWithXpMultiplierEffect() {
        val jsonString = """
            {
                "id": "S1",
                "branch": "STREAK",
                "tier": 1,
                "nameKey": "talent_s1_name",
                "descriptionKey": "talent_s1_desc",
                "costPoints": 1,
                "effect": {
                    "type": "XP_MULTIPLIER",
                    "multiplier": 1.05,
                    "condition": {
                        "type": "STREAK_AT_LEAST",
                        "value": 3
                    }
                }
            }
        """.trimIndent()

        val talent = json.decodeFromString<SeedTalent>(jsonString)

        assertEquals("S1", talent.id)
        assertEquals("STREAK", talent.branch)
        assertEquals("XP_MULTIPLIER", talent.effect.type)
        assertEquals(1.05, talent.effect.multiplier)
        assertEquals("STREAK_AT_LEAST", talent.effect.condition?.type)
        assertEquals(3, talent.effect.condition?.value)
    }

    @Test
    fun parseSeedTalentWithStreakShieldEffect() {
        val jsonString = """
            {
                "id": "S2",
                "branch": "STREAK",
                "tier": 2,
                "nameKey": "talent_s2_name",
                "descriptionKey": "talent_s2_desc",
                "costPoints": 2,
                "effect": {
                    "type": "STREAK_SHIELD",
                    "chargesPerWeek": 1
                }
            }
        """.trimIndent()

        val talent = json.decodeFromString<SeedTalent>(jsonString)

        assertEquals("S2", talent.id)
        assertEquals("STREAK_SHIELD", talent.effect.type)
        assertEquals(1, talent.effect.chargesPerWeek)
    }

    @Test
    fun parseSeedTalentWithRareDropLuckEffect() {
        val jsonString = """
            {
                "id": "T3",
                "branch": "STYLE",
                "tier": 3,
                "nameKey": "talent_t3_name",
                "descriptionKey": "talent_t3_desc",
                "costPoints": 3,
                "effect": {
                    "type": "RARE_DROP_LUCK",
                    "bonusPercent": 20
                }
            }
        """.trimIndent()

        val talent = json.decodeFromString<SeedTalent>(jsonString)

        assertEquals("T3", talent.id)
        assertEquals("RARE_DROP_LUCK", talent.effect.type)
        assertEquals(20, talent.effect.bonusPercent)
    }

    @Test
    fun parseSeedTalentWithSimpleEffect() {
        val jsonString = """
            {
                "id": "T1",
                "branch": "STYLE",
                "tier": 1,
                "nameKey": "talent_t1_name",
                "descriptionKey": "talent_t1_desc",
                "costPoints": 1,
                "effect": {
                    "type": "UNLOCK_THEME_SLOT"
                }
            }
        """.trimIndent()

        val talent = json.decodeFromString<SeedTalent>(jsonString)

        assertEquals("T1", talent.id)
        assertEquals("UNLOCK_THEME_SLOT", talent.effect.type)
    }

    @Test
    fun parseSeedRewardBadge() {
        val jsonString = """
            {
                "id": "badge_first_quest",
                "type": "BADGE",
                "rarity": "COMMON",
                "nameKey": "badge_first_quest_name",
                "assetRef": "badge_first_quest.png"
            }
        """.trimIndent()

        val reward = json.decodeFromString<SeedReward>(jsonString)

        assertEquals("badge_first_quest", reward.id)
        assertEquals("BADGE", reward.type)
        assertEquals("COMMON", reward.rarity)
        assertEquals("badge_first_quest_name", reward.nameKey)
        assertEquals("badge_first_quest.png", reward.assetRef)
    }

    @Test
    fun parseSeedRewardTheme() {
        val jsonString = """
            {
                "id": "theme_lavender",
                "type": "THEME",
                "rarity": "RARE",
                "nameKey": "theme_lavender_name"
            }
        """.trimIndent()

        val reward = json.decodeFromString<SeedReward>(jsonString)

        assertEquals("theme_lavender", reward.id)
        assertEquals("THEME", reward.type)
        assertEquals("RARE", reward.rarity)
    }

    @Test
    fun parseSeedRewardSound() {
        val jsonString = """
            {
                "id": "sound_rain_lofi",
                "type": "SOUND",
                "rarity": "COMMON",
                "nameKey": "sound_rain_lofi_name",
                "assetRef": "rain_lofi.mp3"
            }
        """.trimIndent()

        val reward = json.decodeFromString<SeedReward>(jsonString)

        assertEquals("sound_rain_lofi", reward.id)
        assertEquals("SOUND", reward.type)
        assertEquals("rain_lofi.mp3", reward.assetRef)
    }

    @Test
    fun parseSeedTalentList() {
        val jsonString = """
            [
                {
                    "id": "D1",
                    "branch": "DISCIPLINE",
                    "tier": 1,
                    "nameKey": "n1",
                    "descriptionKey": "d1",
                    "costPoints": 1,
                    "effect": {"type": "ADD_XP", "amount": 5}
                },
                {
                    "id": "D2",
                    "branch": "DISCIPLINE",
                    "tier": 2,
                    "nameKey": "n2",
                    "descriptionKey": "d2",
                    "costPoints": 2,
                    "effect": {"type": "ADD_XP", "amount": 10}
                }
            ]
        """.trimIndent()

        val talents = json.decodeFromString<List<SeedTalent>>(jsonString)

        assertEquals(2, talents.size)
        assertEquals("D1", talents[0].id)
        assertEquals("D2", talents[1].id)
    }

    @Test
    fun parseSeedRewardList() {
        val jsonString = """
            [
                {
                    "id": "badge_1",
                    "type": "BADGE",
                    "rarity": "COMMON",
                    "nameKey": "n1"
                },
                {
                    "id": "badge_2",
                    "type": "BADGE",
                    "rarity": "RARE",
                    "nameKey": "n2"
                }
            ]
        """.trimIndent()

        val rewards = json.decodeFromString<List<SeedReward>>(jsonString)

        assertEquals(2, rewards.size)
        assertEquals("badge_1", rewards[0].id)
        assertEquals("badge_2", rewards[1].id)
    }

    @Test
    fun parseSeedCoachMessages() {
        val jsonString = """
            {
                "success": {
                    "chill": ["Nice work."],
                    "hype": ["Big win!"],
                    "strict": ["Done."],
                    "factual": ["Score: good"],
                    "tips": ["Keep going"]
                },
                "partial": {
                    "chill": ["Almost."],
                    "hype": ["Next time!"],
                    "strict": ["Adjust."],
                    "factual": ["Short by 10 min"],
                    "tips": ["Try earlier"]
                },
                "fail": {
                    "chill": ["Reset."],
                    "hype": ["Comeback!"],
                    "strict": ["Restart."],
                    "factual": ["Failed"],
                    "tips": ["Smaller goal"]
                },
                "recap": {
                    "highlight": ["Great week!"],
                    "objective": ["More sleep"]
                }
            }
        """.trimIndent()

        val messages = json.decodeFromString<SeedCoachMessages>(jsonString)

        assertEquals(1, messages.success.chill.size)
        assertEquals("Nice work.", messages.success.chill[0])
        assertEquals(1, messages.success.hype.size)
        assertEquals("Big win!", messages.success.hype[0])
        assertEquals(1, messages.partial.strict.size)
        assertEquals(1, messages.fail.factual.size)
        assertEquals(1, messages.recap.highlight.size)
        assertEquals("Great week!", messages.recap.highlight[0])
    }

    @Test
    fun parseConditionTypes() {
        val conditions = listOf(
            """{"type": "STREAK_AT_LEAST", "value": 3}""" to "STREAK_AT_LEAST",
            """{"type": "START_WITHIN_MINUTES", "value": 10}""" to "START_WITHIN_MINUTES",
            """{"type": "START_BEFORE_MINUTES", "value": 15}""" to "START_BEFORE_MINUTES",
            """{"type": "SUCCESS_WITH_SCORE_AT_LEAST", "value": 90}""" to "SUCCESS_WITH_SCORE_AT_LEAST",
        )

        for ((jsonString, expectedType) in conditions) {
            val condition = json.decodeFromString<SeedTalentCondition>(jsonString)
            assertEquals(expectedType, condition.type)
        }
    }

    @Test
    fun parseEffectTypes() {
        val effects = listOf(
            """{"type": "ADD_XP", "amount": 5}""" to "ADD_XP",
            """{"type": "XP_MULTIPLIER", "multiplier": 1.1}""" to "XP_MULTIPLIER",
            """{"type": "STREAK_SHIELD", "chargesPerWeek": 1}""" to "STREAK_SHIELD",
            """{"type": "UNLOCK_THEME_SLOT"}""" to "UNLOCK_THEME_SLOT",
            """{"type": "UNLOCK_SOUND_PACK"}""" to "UNLOCK_SOUND_PACK",
            """{"type": "RARE_DROP_LUCK", "bonusPercent": 20}""" to "RARE_DROP_LUCK",
            """{"type": "ENABLE_WEEKLY_RECAP"}""" to "ENABLE_WEEKLY_RECAP",
            """{"type": "ENABLE_ADVANCED_CALENDAR"}""" to "ENABLE_ADVANCED_CALENDAR",
            """{"type": "ENABLE_TRENDLINE"}""" to "ENABLE_TRENDLINE",
        )

        for ((jsonString, expectedType) in effects) {
            val effect = json.decodeFromString<SeedTalentEffect>(jsonString)
            assertEquals(expectedType, effect.type)
        }
    }
}
