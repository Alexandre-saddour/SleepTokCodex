package com.example.domain.scoring

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelProgressTest {

    @Test
    fun level1At0Xp() {
        assertEquals(1, LevelCalculator.calculateLevel(0))
    }

    @Test
    fun level1At299Xp() {
        assertEquals(1, LevelCalculator.calculateLevel(299))
    }

    @Test
    fun level2At300Xp() {
        assertEquals(2, LevelCalculator.calculateLevel(300))
    }

    @Test
    fun level2At699Xp() {
        assertEquals(2, LevelCalculator.calculateLevel(699))
    }

    @Test
    fun level3At700Xp() {
        assertEquals(3, LevelCalculator.calculateLevel(700))
    }

    @Test
    fun level4At1350Xp() {
        assertEquals(4, LevelCalculator.calculateLevel(1350))
    }

    @Test
    fun level5At2000Xp() {
        assertEquals(5, LevelCalculator.calculateLevel(2000))
    }

    @Test
    fun level10At7000Xp() {
        assertEquals(10, LevelCalculator.calculateLevel(7000))
    }

    @Test
    fun level20At25000Xp() {
        assertEquals(20, LevelCalculator.calculateLevel(25000))
    }

    @Test
    fun levelProgressShowsCorrectXpInLevel() {
        val progress = LevelCalculator.levelProgress(500) // Between level 2 and 3
        assertEquals(2, progress.level)
        assertEquals(200, progress.xpInLevel) // 500 - 300 = 200
    }

    @Test
    fun levelProgressShowsCorrectLevelSpan() {
        val progress = LevelCalculator.levelProgress(500)
        assertEquals(400, progress.levelSpan) // 700 - 300 = 400
    }

    @Test
    fun levelProgressShowsCorrectNextLevelXp() {
        val progress = LevelCalculator.levelProgress(500)
        assertEquals(700, progress.nextLevelXp)
    }

    @Test
    fun levelUpdateGrantsTalentPointsOnLevelUp() {
        val update = LevelCalculator.applyLevelUpdate(
            currentLevel = 1,
            currentTalentPoints = 0,
            xpTotal = 700, // Level 3
        )
        assertEquals(3, update.level)
        assertEquals(2, update.talentPointsAvailable) // +2 for 2 level ups
    }

    @Test
    fun levelUpdateDoesNotGrantPointsIfNoLevelUp() {
        val update = LevelCalculator.applyLevelUpdate(
            currentLevel = 2,
            currentTalentPoints = 1,
            xpTotal = 500, // Still level 2
        )
        assertEquals(2, update.level)
        assertEquals(1, update.talentPointsAvailable) // No change
    }

    @Test
    fun levelUpdateHandlesMultipleLevelUps() {
        val update = LevelCalculator.applyLevelUpdate(
            currentLevel = 1,
            currentTalentPoints = 0,
            xpTotal = 7000, // Level 10
        )
        assertEquals(10, update.level)
        assertEquals(9, update.talentPointsAvailable) // +9 for 9 level ups
    }

    @Test
    fun levelProgressAtExactThreshold() {
        val progress = LevelCalculator.levelProgress(700)
        assertEquals(3, progress.level)
        assertEquals(0, progress.xpInLevel) // Exactly at threshold
    }

    @Test
    fun levelProgressAtLevel20() {
        val progress = LevelCalculator.levelProgress(25000)
        assertEquals(20, progress.level)
        assertEquals(0, progress.xpInLevel)
    }

    @Test
    fun levelProgressBeyond20() {
        val progress = LevelCalculator.levelProgress(30000)
        assertEquals(20, progress.level) // Capped at 20 for now
        assertTrue(progress.xpTotal == 30000L)
    }

    @Test
    fun levelIsMonotonicallyIncreasing() {
        var previousLevel = 0
        val xpValues = listOf(0L, 100L, 300L, 500L, 700L, 1000L, 2000L, 5000L, 7000L, 15000L, 25000L)
        for (xp in xpValues) {
            val level = LevelCalculator.calculateLevel(xp)
            assertTrue(level >= previousLevel)
            previousLevel = level
        }
    }

    @Test
    fun intermediateLevel4CalculatedCorrectly() {
        // Between level 3 (700 XP) and level 5 (2000 XP)
        // Level 4 should be at approximately 1350 XP (midpoint interpolation)
        val level = LevelCalculator.calculateLevel(1350)
        assertEquals(4, level)
    }

    @Test
    fun intermediateLevel7CalculatedCorrectly() {
        // Between level 5 (2000 XP) and level 10 (7000 XP)
        // 5 levels, 5000 XP span = 1000 XP per level
        // Level 7 = 2000 + 2*1000 = 4000 XP
        val level = LevelCalculator.calculateLevel(4000)
        assertEquals(7, level)
    }

    @Test
    fun intermediateLevel15CalculatedCorrectly() {
        // Between level 10 (7000 XP) and level 20 (25000 XP)
        // 10 levels, 18000 XP span = 1800 XP per level
        // Level 15 = 7000 + 5*1800 = 16000 XP
        val level = LevelCalculator.calculateLevel(16000)
        assertEquals(15, level)
    }
}
