package com.example.domain.scoring

import com.example.domain.model.NightResult
import com.example.domain.model.NightStatus
import com.example.domain.model.Talent
import com.example.domain.model.TalentCondition
import com.example.domain.model.TalentEffect
import com.example.domain.model.XpBreakdown
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DefaultNightResultCalculator : NightResultCalculator {
    override fun calculate(input: NightScoreInput): NightResult {
        val plan = input.plan
        val night = input.night
        val endAt = requireNotNull(night.endAt)
        val timeZone = input.timeZone
        val localStart = night.startAt.toLocalDateTime(timeZone)
        val localEnd = endAt.toLocalDateTime(timeZone)

        val planStartDate = resolvePlanStartDate(localStart, plan.planStartLocalTime, plan.planEndLocalTime)
        val planStartDateTime = LocalDateTime(planStartDate, plan.planStartLocalTime)
        val planEndDateTime = LocalDateTime(
            resolvePlanEndDate(planStartDate, plan.planStartLocalTime, plan.planEndLocalTime),
            plan.planEndLocalTime,
        )

        val planDurationMinutes = plan.durationMinutes
        val actualDurationMinutes = night.actualDurationMinutes
            ?: durationMinutesBetween(night.startAt, endAt)
        val deltaStartMinutes = minutesBetween(planStartDateTime, localStart, timeZone)
        val deltaEndMinutes = minutesBetween(planEndDateTime, localEnd, timeZone)

        val status = computeStatus(
            actualDurationMinutes = actualDurationMinutes,
            planDurationMinutes = planDurationMinutes,
            deltaStartMinutes = deltaStartMinutes,
            deltaEndMinutes = deltaEndMinutes,
            toleranceStartMinutes = plan.toleranceStartMinutes,
            toleranceEndMinutes = plan.toleranceEndMinutes,
        )

        val score = computeScore(
            actualDurationMinutes = actualDurationMinutes,
            planDurationMinutes = planDurationMinutes,
            deltaStartMinutes = deltaStartMinutes,
            deltaEndMinutes = deltaEndMinutes,
        )

        val streakAfter = if (status == NightStatus.SUCCESS) input.streakBefore + 1 else 0
        val xpBreakdown = computeXpBreakdown(
            status = status,
            score = score,
            deltaStartMinutes = deltaStartMinutes,
            streakAfter = streakAfter,
            unlockedTalents = input.unlockedTalents,
        )

        return NightResult(
            status = status,
            score = score,
            xpBreakdown = xpBreakdown,
            streakBefore = input.streakBefore,
            streakAfter = streakAfter,
            planDurationMinutes = planDurationMinutes,
            actualDurationMinutes = actualDurationMinutes,
            deltaStartMinutes = deltaStartMinutes,
            deltaEndMinutes = deltaEndMinutes,
        )
    }

    private fun resolvePlanStartDate(
        actualStart: LocalDateTime,
        planStart: LocalTime,
        planEnd: LocalTime,
    ): LocalDate {
        val crossesMidnight = planEnd <= planStart
        return if (crossesMidnight && actualStart.time < planStart) {
            actualStart.date.minus(1, DateTimeUnit.DAY)
        } else {
            actualStart.date
        }
    }

    private fun resolvePlanEndDate(
        planStartDate: LocalDate,
        planStart: LocalTime,
        planEnd: LocalTime,
    ): LocalDate {
        return if (planEnd <= planStart) {
            planStartDate.plus(1, DateTimeUnit.DAY)
        } else {
            planStartDate
        }
    }

    private fun durationMinutesBetween(start: Instant, end: Instant): Int {
        val millis = end.toEpochMilliseconds() - start.toEpochMilliseconds()
        return (millis / 60000L).toInt().coerceAtLeast(0)
    }

    private fun minutesBetween(start: LocalDateTime, end: LocalDateTime, timeZone: TimeZone): Int {
        val startInstant = start.toInstant(timeZone)
        val endInstant = end.toInstant(timeZone)
        val millis = endInstant.toEpochMilliseconds() - startInstant.toEpochMilliseconds()
        return (millis / 60000L).toInt()
    }

    private fun computeStatus(
        actualDurationMinutes: Int,
        planDurationMinutes: Int,
        deltaStartMinutes: Int,
        deltaEndMinutes: Int,
        toleranceStartMinutes: Int,
        toleranceEndMinutes: Int,
    ): NightStatus {
        val withinStart = abs(deltaStartMinutes) <= toleranceStartMinutes
        val withinEnd = abs(deltaEndMinutes) <= toleranceEndMinutes
        val successDuration = actualDurationMinutes >= planDurationMinutes - 10
        val partialDuration = actualDurationMinutes >= planDurationMinutes - 45
        return when {
            successDuration && withinStart && withinEnd -> NightStatus.SUCCESS
            partialDuration -> NightStatus.PARTIAL
            else -> NightStatus.FAIL
        }
    }

    private fun computeScore(
        actualDurationMinutes: Int,
        planDurationMinutes: Int,
        deltaStartMinutes: Int,
        deltaEndMinutes: Int,
    ): Int {
        val durationRatio = if (planDurationMinutes == 0) 0.0 else actualDurationMinutes.toDouble() / planDurationMinutes
        val durationPoints = durationRatio.coerceIn(0.0, 1.0) * 60.0
        val startPoints = 20.0 - (abs(deltaStartMinutes) * 20.0 / 30.0)
        val endPoints = 20.0 - (abs(deltaEndMinutes) * 20.0 / 40.0)
        val total = durationPoints + startPoints.coerceAtLeast(0.0) + endPoints.coerceAtLeast(0.0)
        return total.roundToInt().coerceIn(0, 100)
    }

    private fun computeXpBreakdown(
        status: NightStatus,
        score: Int,
        deltaStartMinutes: Int,
        streakAfter: Int,
        unlockedTalents: List<Talent>,
    ): XpBreakdown {
        val baseXp = when (status) {
            NightStatus.SUCCESS -> 100
            NightStatus.PARTIAL -> 40
            NightStatus.FAIL -> 10
            NightStatus.IN_PROGRESS -> 0
            NightStatus.VOID -> 0
        }
        val scoreBonus = (score / 10) * 2
        val perfectBonus = if (status == NightStatus.SUCCESS && score >= 90) 25 else 0
        val talentAdditions = computeTalentAdditions(status, score, deltaStartMinutes, streakAfter, unlockedTalents)
        val xpRaw = baseXp + scoreBonus + perfectBonus + talentAdditions
        val streakMultiplier = streakMultiplierFor(streakAfter)
        val talentMultiplier = talentMultiplierFor(status, score, deltaStartMinutes, streakAfter, unlockedTalents)
        val totalXp = floor(xpRaw * streakMultiplier * talentMultiplier).toInt()
        return XpBreakdown(
            baseXp = baseXp,
            scoreBonusXp = scoreBonus,
            perfectBonusXp = perfectBonus,
            talentAdditionsXp = talentAdditions,
            streakMultiplier = streakMultiplier,
            talentMultiplier = talentMultiplier,
            totalXp = totalXp,
        )
    }

    private fun computeTalentAdditions(
        status: NightStatus,
        score: Int,
        deltaStartMinutes: Int,
        streakAfter: Int,
        unlockedTalents: List<Talent>,
    ): Int {
        return unlockedTalents
            .asSequence()
            .map { it.effect }
            .filterIsInstance<TalentEffect.AddXp>()
            .filter { evaluateCondition(it.condition, status, score, deltaStartMinutes, streakAfter) }
            .sumOf { it.amount }
    }

    private fun streakMultiplierFor(streakAfter: Int): Double {
        return when {
            streakAfter >= 30 -> 1.4
            streakAfter >= 14 -> 1.3
            streakAfter >= 7 -> 1.2
            streakAfter >= 3 -> 1.1
            else -> 1.0
        }
    }

    private fun talentMultiplierFor(
        status: NightStatus,
        score: Int,
        deltaStartMinutes: Int,
        streakAfter: Int,
        unlockedTalents: List<Talent>,
    ): Double {
        return unlockedTalents
            .asSequence()
            .map { it.effect }
            .filterIsInstance<TalentEffect.XpMultiplier>()
            .filter { evaluateCondition(it.condition, status, score, deltaStartMinutes, streakAfter) }
            .fold(1.0) { acc, effect -> acc * effect.multiplier }
    }

    private fun evaluateCondition(
        condition: TalentCondition?,
        status: NightStatus,
        score: Int,
        deltaStartMinutes: Int,
        streakAfter: Int,
    ): Boolean {
        return when (condition) {
            null -> true
            is TalentCondition.Always -> true
            is TalentCondition.StreakAtLeast -> streakAfter >= condition.days
            is TalentCondition.StartWithinMinutes -> abs(deltaStartMinutes) <= condition.minutes
            is TalentCondition.StartBeforeMinutes -> deltaStartMinutes <= condition.minutesAfterPlan
            is TalentCondition.SuccessWithScoreAtLeast -> status == NightStatus.SUCCESS && score >= condition.score
        }
    }
}
