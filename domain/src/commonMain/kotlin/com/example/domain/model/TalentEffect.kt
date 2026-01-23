package com.example.domain.model

sealed interface TalentEffect {
    data object None : TalentEffect

    data class AddXp(
        val amount: Int,
        val condition: TalentCondition,
    ) : TalentEffect

    data class XpMultiplier(
        val multiplier: Double,
        val condition: TalentCondition,
    ) : TalentEffect

    data class StreakShield(
        val chargesPerWeek: Int,
    ) : TalentEffect

    data object UnlockThemeSlot : TalentEffect

    data object UnlockSoundPack : TalentEffect

    data class RareDropLuck(
        val bonusPercent: Int,
    ) : TalentEffect

    data object EnableWeeklyRecap : TalentEffect

    data object EnableAdvancedCalendar : TalentEffect

    data object EnableTrendline : TalentEffect
}

sealed interface TalentCondition {
    data object Always : TalentCondition
    data class StreakAtLeast(val days: Int) : TalentCondition
    data class StartWithinMinutes(val minutes: Int) : TalentCondition
    data class StartBeforeMinutes(val minutesAfterPlan: Int) : TalentCondition
    data class SuccessWithScoreAtLeast(val score: Int) : TalentCondition
}
