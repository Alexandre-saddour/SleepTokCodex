package com.example.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface TalentEffect {
    @Serializable
    @SerialName("NONE")
    data object None : TalentEffect

    @Serializable
    @SerialName("ADD_XP")
    data class AddXp(
        val amount: Int,
        val condition: TalentCondition,
    ) : TalentEffect

    @Serializable
    @SerialName("XP_MULTIPLIER")
    data class XpMultiplier(
        val multiplier: Double,
        val condition: TalentCondition,
    ) : TalentEffect

    @Serializable
    @SerialName("STREAK_SHIELD")
    data class StreakShield(
        val chargesPerWeek: Int,
    ) : TalentEffect

    @Serializable
    @SerialName("UNLOCK_THEME_SLOT")
    data object UnlockThemeSlot : TalentEffect

    @Serializable
    @SerialName("UNLOCK_SOUND_PACK")
    data object UnlockSoundPack : TalentEffect

    @Serializable
    @SerialName("RARE_DROP_LUCK")
    data class RareDropLuck(
        val bonusPercent: Int,
    ) : TalentEffect

    @Serializable
    @SerialName("ENABLE_WEEKLY_RECAP")
    data object EnableWeeklyRecap : TalentEffect

    @Serializable
    @SerialName("ENABLE_ADVANCED_CALENDAR")
    data object EnableAdvancedCalendar : TalentEffect

    @Serializable
    @SerialName("ENABLE_TRENDLINE")
    data object EnableTrendline : TalentEffect
}

@Serializable
sealed interface TalentCondition {
    @Serializable
    @SerialName("ALWAYS")
    data object Always : TalentCondition

    @Serializable
    @SerialName("STREAK_AT_LEAST")
    data class StreakAtLeast(
        @SerialName("value") val days: Int,
    ) : TalentCondition

    @Serializable
    @SerialName("START_WITHIN_MINUTES")
    data class StartWithinMinutes(
        @SerialName("value") val minutes: Int,
    ) : TalentCondition

    @Serializable
    @SerialName("START_BEFORE_MINUTES")
    data class StartBeforeMinutes(
        @SerialName("value") val minutesAfterPlan: Int,
    ) : TalentCondition

    @Serializable
    @SerialName("SUCCESS_WITH_SCORE_AT_LEAST")
    data class SuccessWithScoreAtLeast(
        @SerialName("value") val score: Int,
    ) : TalentCondition
}
