package com.example.domain.scoring

import com.example.domain.model.NightResult
import com.example.domain.model.NightStatus
import com.example.domain.model.XpBreakdown
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

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

        val planDurationMinutes = computePlanDurationMinutes(plan.planStartLocalTime, plan.planEndLocalTime)
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
            unlockedTalentIds = input.unlockedTalentIds,
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

    private fun computePlanDurationMinutes(start: LocalTime, end: LocalTime): Int {
        val startMinutes = start.hour * 60 + start.minute
        val endMinutes = end.hour * 60 + end.minute
        return if (endMinutes >= startMinutes) {
            endMinutes - startMinutes
        } else {
            (24 * 60 - startMinutes) + endMinutes
        }
    }

    private fun durationMinutesBetween(start: kotlinx.datetime.Instant, end: kotlinx.datetime.Instant): Int {
        val millis = end.toEpochMilliseconds() - start.toEpochMilliseconds()
        return (millis / 60000L).toInt().coerceAtLeast(0)
    }

    private fun minutesBetween(start: LocalDateTime, end: LocalDateTime, timeZone: TimeZone): Int {
        return start.until(end, DateTimeUnit.MINUTE, timeZone).toInt()
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
        unlockedTalentIds: Set<String>,
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
        val talentAdditions = computeTalentAdditions(status, score, deltaStartMinutes, unlockedTalentIds)
        val xpRaw = baseXp + scoreBonus + perfectBonus + talentAdditions
        val streakMultiplier = streakMultiplierFor(streakAfter)
        val talentMultiplier = talentMultiplierFor(streakAfter, unlockedTalentIds)
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
        unlockedTalentIds: Set<String>,
    ): Int {
        var total = 0
        if (unlockedTalentIds.contains("D1") && deltaStartMinutes <= 10) {
            total += 5
        }
        if (unlockedTalentIds.contains("D2") && abs(deltaStartMinutes) <= 15) {
            total += 10
        }
        if (unlockedTalentIds.contains("D3") && status == NightStatus.SUCCESS && score >= 90) {
            total += 25
        }
        return total
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

    private fun talentMultiplierFor(streakAfter: Int, unlockedTalentIds: Set<String>): Double {
        return when {
            unlockedTalentIds.contains("S3") && streakAfter >= 7 -> 1.10
            unlockedTalentIds.contains("S1") && streakAfter >= 3 -> 1.05
            else -> 1.0
        }
    }
}
