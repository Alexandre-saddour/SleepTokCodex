package com.example.domain.scoring

import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.model.SleepPlan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

class NightResultCalculatorTest {
    private val calculator = DefaultNightResultCalculator()

    @Test
    fun calculatesSuccessStatusAndXp() {
        val plan = basePlan()
        val night = baseNight(
            startAt = Instant.parse("2025-01-01T23:35:00Z"),
            endAt = Instant.parse("2025-01-02T07:25:00Z"),
        )
        val result = calculator.calculate(
            NightScoreInput(
                plan = plan,
                night = night,
                timeZone = TimeZone.UTC,
                streakBefore = 2,
                unlockedTalentIds = emptySet(),
            )
        )
        assertEquals(NightStatus.SUCCESS, result.status)
        assertEquals(93, result.score)
        assertEquals(157, result.xpBreakdown.totalXp)
        assertEquals(3, result.streakAfter)
    }

    @Test
    fun calculatesPartialStatus() {
        val plan = basePlan()
        val night = baseNight(
            startAt = Instant.parse("2025-01-01T23:50:00Z"),
            endAt = Instant.parse("2025-01-02T07:10:00Z"),
        )
        val result = calculator.calculate(
            NightScoreInput(
                plan = plan,
                night = night,
                timeZone = TimeZone.UTC,
                streakBefore = 1,
                unlockedTalentIds = emptySet(),
            )
        )
        assertEquals(NightStatus.PARTIAL, result.status)
        assertEquals(40, result.xpBreakdown.baseXp)
    }

    @Test
    fun calculatesFailStatus() {
        val plan = basePlan()
        val night = baseNight(
            startAt = Instant.parse("2025-01-02T00:30:00Z"),
            endAt = Instant.parse("2025-01-02T07:10:00Z"),
        )
        val result = calculator.calculate(
            NightScoreInput(
                plan = plan,
                night = night,
                timeZone = TimeZone.UTC,
                streakBefore = 4,
                unlockedTalentIds = emptySet(),
            )
        )
        assertEquals(NightStatus.FAIL, result.status)
        assertEquals(10, result.xpBreakdown.baseXp)
        assertEquals(0, result.streakAfter)
    }

    @Test
    fun appliesTalentBonusesAndMultipliers() {
        val plan = basePlan()
        val night = baseNight(
            startAt = Instant.parse("2025-01-01T23:35:00Z"),
            endAt = Instant.parse("2025-01-02T07:25:00Z"),
        )
        val result = calculator.calculate(
            NightScoreInput(
                plan = plan,
                night = night,
                timeZone = TimeZone.UTC,
                streakBefore = 6,
                unlockedTalentIds = setOf("D1", "D2", "D3", "S1", "S3"),
            )
        )
        assertEquals(40, result.xpBreakdown.talentAdditionsXp)
        assertEquals(1.2, result.xpBreakdown.streakMultiplier)
        assertEquals(1.10, result.xpBreakdown.talentMultiplier)
        assertEquals(241, result.xpBreakdown.totalXp)
    }

    private fun basePlan(): SleepPlan {
        return SleepPlan(
            id = 1L,
            userId = 1L,
            planStartLocalTime = LocalTime(hour = 23, minute = 30),
            planEndLocalTime = LocalTime(hour = 7, minute = 30),
            activeDaysMask = 0,
            toleranceStartMinutes = 15,
            toleranceEndMinutes = 20,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            isActive = true,
        )
    }

    private fun baseNight(startAt: Instant, endAt: Instant): Night {
        return Night(
            id = 1L,
            userId = 1L,
            planId = 1L,
            startAt = startAt,
            endAt = endAt,
            status = NightStatus.IN_PROGRESS,
            actualDurationMinutes = null,
            planDurationMinutes = 0,
            score = null,
            xpEarned = null,
            streakBefore = null,
            streakAfter = null,
            createdAt = startAt,
            note = null,
        )
    }
}
