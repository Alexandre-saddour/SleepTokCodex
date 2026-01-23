package com.example.data.seed

import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TalentEntity
import com.example.data.mapper.toJson
import com.example.domain.model.RewardRarity
import com.example.domain.model.RewardType
import com.example.domain.model.TalentBranch
import com.example.domain.model.TalentCondition
import com.example.domain.model.TalentEffect
import com.example.domain.model.TalentTier

fun SeedTalent.toEntity(): TalentEntity {
    val effect = effect.toDomainEffect()
    return TalentEntity(
        id = id,
        branch = TalentBranch.valueOf(branch),
        tier = tier.toTalentTier(),
        nameKey = nameKey,
        descriptionKey = descriptionKey,
        costPoints = costPoints,
        effectJson = effect.toJson(),
        isActive = isActive,
    )
}

fun SeedReward.toEntity(): RewardEntity {
    return RewardEntity(
        id = id,
        type = RewardType.valueOf(type),
        rarity = RewardRarity.valueOf(rarity),
        nameKey = nameKey,
        assetRef = assetRef,
        metaJson = metaJson,
    )
}

private fun SeedTalentEffect.toDomainEffect(): TalentEffect {
    return when (type) {
        "ADD_XP" -> TalentEffect.AddXp(
            amount = amount ?: 0,
            condition = condition?.toDomainCondition() ?: TalentCondition.Always,
        )
        "XP_MULTIPLIER" -> TalentEffect.XpMultiplier(
            multiplier = multiplier ?: 1.0,
            condition = condition?.toDomainCondition() ?: TalentCondition.Always,
        )
        "STREAK_SHIELD" -> TalentEffect.StreakShield(chargesPerWeek = chargesPerWeek ?: 0)
        "UNLOCK_THEME_SLOT" -> TalentEffect.UnlockThemeSlot
        "UNLOCK_SOUND_PACK" -> TalentEffect.UnlockSoundPack
        "RARE_DROP_LUCK" -> TalentEffect.RareDropLuck(bonusPercent = bonusPercent ?: 0)
        "ENABLE_WEEKLY_RECAP" -> TalentEffect.EnableWeeklyRecap
        "ENABLE_ADVANCED_CALENDAR" -> TalentEffect.EnableAdvancedCalendar
        "ENABLE_TRENDLINE" -> TalentEffect.EnableTrendline
        else -> TalentEffect.None
    }
}

private fun SeedTalentCondition.toDomainCondition(): TalentCondition {
    return when (type) {
        "STREAK_AT_LEAST" -> TalentCondition.StreakAtLeast(value ?: 0)
        "START_WITHIN_MINUTES" -> TalentCondition.StartWithinMinutes(value ?: 0)
        "START_BEFORE_MINUTES" -> TalentCondition.StartBeforeMinutes(value ?: 0)
        "SUCCESS_WITH_SCORE_AT_LEAST" -> TalentCondition.SuccessWithScoreAtLeast(value ?: 0)
        else -> TalentCondition.Always
    }
}

private fun Int.toTalentTier(): TalentTier {
    return when (this) {
        1 -> TalentTier.TIER_1
        2 -> TalentTier.TIER_2
        3 -> TalentTier.TIER_3
        else -> TalentTier.TIER_1
    }
}
