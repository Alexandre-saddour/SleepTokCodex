package com.example.domain.model

data class NightResult(
    val status: NightStatus,
    val score: Int,
    val xpBreakdown: XpBreakdown,
    val streakBefore: Int,
    val streakAfter: Int,
    val planDurationMinutes: Int,
    val actualDurationMinutes: Int,
    val deltaStartMinutes: Int,
    val deltaEndMinutes: Int,
)

data class XpBreakdown(
    val baseXp: Int,
    val scoreBonusXp: Int,
    val perfectBonusXp: Int,
    val talentAdditionsXp: Int,
    val streakMultiplier: Double,
    val talentMultiplier: Double,
    val totalXp: Int,
)
