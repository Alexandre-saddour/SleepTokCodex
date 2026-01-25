package com.example.domain.scoring

import kotlin.math.roundToInt

object LevelCalculator {
    private val thresholds: List<LevelThreshold> = buildThresholds()

    fun calculateLevel(xpTotal: Long): Int {
        return thresholds
            .filter { xpTotal >= it.xpRequired }
            .maxByOrNull { it.level }
            ?.level
            ?: 1
    }

    fun applyLevelUpdate(currentLevel: Int, currentTalentPoints: Int, xpTotal: Long): LevelUpdate {
        val newLevel = calculateLevel(xpTotal)
        val gainedLevels = (newLevel - currentLevel).coerceAtLeast(0)
        return LevelUpdate(
            level = newLevel,
            talentPointsAvailable = currentTalentPoints + gainedLevels,
        )
    }

    fun levelProgress(xpTotal: Long): LevelProgress {
        val level = calculateLevel(xpTotal)
        val currentThreshold = thresholds.last { it.level <= level }
        val nextThreshold = thresholds.firstOrNull { it.level > level } ?: currentThreshold
        val xpInLevel = (xpTotal - currentThreshold.xpRequired).coerceAtLeast(0).toInt()
        val levelSpan = (nextThreshold.xpRequired - currentThreshold.xpRequired).coerceAtLeast(1)
        return LevelProgress(
            level = level,
            xpInLevel = xpInLevel,
            levelSpan = levelSpan,
            xpTotal = xpTotal,
            nextLevelXp = nextThreshold.xpRequired,
        )
    }

    private fun buildThresholds(): List<LevelThreshold> {
        val anchors = listOf(
            LevelThreshold(level = 1, xpRequired = 0),
            LevelThreshold(level = 2, xpRequired = 300),
            LevelThreshold(level = 3, xpRequired = 700),
            LevelThreshold(level = 5, xpRequired = 2000),
            LevelThreshold(level = 10, xpRequired = 7000),
            LevelThreshold(level = 20, xpRequired = 25000),
        )
        val thresholdMap = mutableMapOf<Int, Int>()
        for (index in 0 until anchors.size - 1) {
            val start = anchors[index]
            val end = anchors[index + 1]
            val levelSpan = end.level - start.level
            val xpSpan = end.xpRequired - start.xpRequired
            val step = if (levelSpan == 0) 0.0 else xpSpan.toDouble() / levelSpan.toDouble()
            for (level in start.level..end.level) {
                val offset = level - start.level
                val xpValue = (start.xpRequired + step * offset).roundToInt()
                thresholdMap[level] = xpValue
            }
        }
        return thresholdMap.entries
            .sortedBy { it.key }
            .map { LevelThreshold(it.key, it.value) }
    }
}

data class LevelThreshold(
    val level: Int,
    val xpRequired: Int,
)

data class LevelUpdate(
    val level: Int,
    val talentPointsAvailable: Int,
)

data class LevelProgress(
    val level: Int,
    val xpInLevel: Int,
    val levelSpan: Int,
    val xpTotal: Long,
    val nextLevelXp: Int,
)
