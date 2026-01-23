package com.example.data.mapper

import com.example.domain.model.TalentCondition
import com.example.domain.model.TalentEffect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.parseToJsonElement

private val json = Json { ignoreUnknownKeys = true }

fun TalentEffect.toJson(): String {
    val obj = buildJsonObject {
        when (this@toJson) {
            is TalentEffect.None -> put("type", JsonPrimitive("NONE"))
            is TalentEffect.AddXp -> {
                put("type", JsonPrimitive("ADD_XP"))
                put("amount", JsonPrimitive(amount))
                putCondition(condition)
            }
            is TalentEffect.XpMultiplier -> {
                put("type", JsonPrimitive("XP_MULTIPLIER"))
                put("multiplier", JsonPrimitive(multiplier))
                putCondition(condition)
            }
            is TalentEffect.StreakShield -> {
                put("type", JsonPrimitive("STREAK_SHIELD"))
                put("chargesPerWeek", JsonPrimitive(chargesPerWeek))
            }
            is TalentEffect.UnlockThemeSlot -> put("type", JsonPrimitive("UNLOCK_THEME_SLOT"))
            is TalentEffect.UnlockSoundPack -> put("type", JsonPrimitive("UNLOCK_SOUND_PACK"))
            is TalentEffect.RareDropLuck -> {
                put("type", JsonPrimitive("RARE_DROP_LUCK"))
                put("bonusPercent", JsonPrimitive(bonusPercent))
            }
            is TalentEffect.EnableWeeklyRecap -> put("type", JsonPrimitive("ENABLE_WEEKLY_RECAP"))
            is TalentEffect.EnableAdvancedCalendar -> put("type", JsonPrimitive("ENABLE_ADVANCED_CALENDAR"))
            is TalentEffect.EnableTrendline -> put("type", JsonPrimitive("ENABLE_TRENDLINE"))
        }
    }
    return json.encodeToString(JsonObject.serializer(), obj)
}

fun String.toTalentEffect(): TalentEffect {
    val root = json.parseToJsonElement(this).jsonObject
    val type = root["type"]?.jsonPrimitive?.content ?: return TalentEffect.None
    return when (type) {
        "ADD_XP" -> {
            TalentEffect.AddXp(
                amount = root["amount"]?.jsonPrimitive?.intOrNull ?: 0,
                condition = root.parseCondition(),
            )
        }
        "XP_MULTIPLIER" -> {
            TalentEffect.XpMultiplier(
                multiplier = root["multiplier"]?.jsonPrimitive?.doubleOrNull ?: 1.0,
                condition = root.parseCondition(),
            )
        }
        "STREAK_SHIELD" -> TalentEffect.StreakShield(
            chargesPerWeek = root["chargesPerWeek"]?.jsonPrimitive?.intOrNull ?: 0,
        )
        "UNLOCK_THEME_SLOT" -> TalentEffect.UnlockThemeSlot
        "UNLOCK_SOUND_PACK" -> TalentEffect.UnlockSoundPack
        "RARE_DROP_LUCK" -> TalentEffect.RareDropLuck(
            bonusPercent = root["bonusPercent"]?.jsonPrimitive?.intOrNull ?: 0,
        )
        "ENABLE_WEEKLY_RECAP" -> TalentEffect.EnableWeeklyRecap
        "ENABLE_ADVANCED_CALENDAR" -> TalentEffect.EnableAdvancedCalendar
        "ENABLE_TRENDLINE" -> TalentEffect.EnableTrendline
        else -> TalentEffect.None
    }
}

private fun JsonObject.parseCondition(): TalentCondition {
    val conditionObj = this["condition"]?.jsonObject ?: return TalentCondition.Always
    val type = conditionObj["type"]?.jsonPrimitive?.content ?: return TalentCondition.Always
    val value = conditionObj["value"]?.jsonPrimitive?.intOrNull ?: 0
    return when (type) {
        "STREAK_AT_LEAST" -> TalentCondition.StreakAtLeast(value)
        "START_WITHIN_MINUTES" -> TalentCondition.StartWithinMinutes(value)
        "START_BEFORE_MINUTES" -> TalentCondition.StartBeforeMinutes(value)
        "SUCCESS_WITH_SCORE_AT_LEAST" -> TalentCondition.SuccessWithScoreAtLeast(value)
        "ALWAYS" -> TalentCondition.Always
        else -> TalentCondition.Always
    }
}

private fun kotlinx.serialization.json.JsonObjectBuilder.putCondition(condition: TalentCondition) {
    if (condition == TalentCondition.Always) {
        return
    }
    val conditionObj = buildJsonObject {
        when (condition) {
            TalentCondition.Always -> put("type", JsonPrimitive("ALWAYS"))
            is TalentCondition.StreakAtLeast -> {
                put("type", JsonPrimitive("STREAK_AT_LEAST"))
                put("value", JsonPrimitive(condition.days))
            }
            is TalentCondition.StartWithinMinutes -> {
                put("type", JsonPrimitive("START_WITHIN_MINUTES"))
                put("value", JsonPrimitive(condition.minutes))
            }
            is TalentCondition.StartBeforeMinutes -> {
                put("type", JsonPrimitive("START_BEFORE_MINUTES"))
                put("value", JsonPrimitive(condition.minutesAfterPlan))
            }
            is TalentCondition.SuccessWithScoreAtLeast -> {
                put("type", JsonPrimitive("SUCCESS_WITH_SCORE_AT_LEAST"))
                put("value", JsonPrimitive(condition.score))
            }
        }
    }
    put("condition", conditionObj)
}
