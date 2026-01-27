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
        val xpInLevel = (xpTotal - currentThreshold.xpRequired).coerceAtLeast(0L)
        val levelSpan = (nextThreshold.xpRequired - currentThreshold.xpRequired).coerceAtLeast(1L)
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
            LevelThreshold(level = 1, xpRequired = 0L),
            LevelThreshold(level = 2, xpRequired = 300L),
            LevelThreshold(level = 3, xpRequired = 700L),
            LevelThreshold(level = 5, xpRequired = 2000L),
            LevelThreshold(level = 10, xpRequired = 7000L),
            LevelThreshold(level = 20, xpRequired = 25000L),
        )
        val thresholdMap = mutableMapOf<Int, Long>()
        for (index in 0 until anchors.size - 1) {
            val start = anchors[index]
            val end = anchors[index + 1]
            val levelSpan = end.level - start.level
            val xpSpan = end.xpRequired - start.xpRequired
            val step = if (levelSpan == 0) 0.0 else xpSpan.toDouble() / levelSpan.toDouble()
            for (level in start.level..end.level) {
                val offset = level - start.level
                val xpValue = (start.xpRequired + step * offset).toLong()
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
    val xpRequired: Long,
)

data class LevelUpdate(
    val level: Int,
    val talentPointsAvailable: Int,
)

data class LevelProgress(
    val level: Int,
    val xpInLevel: Long,
    val levelSpan: Long,
    val xpTotal: Long,
    val nextLevelXp: Long,
)
