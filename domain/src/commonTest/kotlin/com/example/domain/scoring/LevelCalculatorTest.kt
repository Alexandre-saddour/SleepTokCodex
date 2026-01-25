package com.example.domain.scoring

import kotlin.test.Test
import kotlin.test.assertEquals

class LevelCalculatorTest {
    @Test
    fun calculatesLevelsFromAnchors() {
        assertEquals(1, LevelCalculator.calculateLevel(0))
        assertEquals(2, LevelCalculator.calculateLevel(300))
        assertEquals(3, LevelCalculator.calculateLevel(700))
        assertEquals(4, LevelCalculator.calculateLevel(1350))
        assertEquals(5, LevelCalculator.calculateLevel(2000))
        assertEquals(10, LevelCalculator.calculateLevel(7000))
    }

    @Test
    fun appliesTalentPointsOnLevelUp() {
        val update = LevelCalculator.applyLevelUpdate(currentLevel = 2, currentTalentPoints = 1, xpTotal = 1350)
        assertEquals(4, update.level)
        assertEquals(3, update.talentPointsAvailable)
    }
}
