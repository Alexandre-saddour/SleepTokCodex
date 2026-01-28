package com.example.domain.scoring

import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.model.SleepPlan
import com.example.domain.model.Talent
import com.example.domain.model.TalentBranch
import com.example.domain.model.TalentCondition
import com.example.domain.model.TalentEffect
import com.example.domain.model.TalentTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
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
                unlockedTalents = emptyList(),
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
                unlockedTalents = emptyList(),
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
                unlockedTalents = emptyList(),
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
        
        // Define talents with effects that match the old hardcoded logic
        val talents = listOf(
            createTalent("D1", TalentEffect.AddXp(5, TalentCondition.StartWithinMinutes(10))),
            createTalent("D2", TalentEffect.AddXp(10, TalentCondition.StartWithinMinutes(15))),
            createTalent("D3", TalentEffect.AddXp(25, TalentCondition.SuccessWithScoreAtLeast(90))),
            createTalent("S1", TalentEffect.XpMultiplier(1.05, TalentCondition.StreakAtLeast(3))),
            createTalent("S3", TalentEffect.XpMultiplier(1.10, TalentCondition.StreakAtLeast(7)))
        )

        val result = calculator.calculate(
            NightScoreInput(
                plan = plan,
                night = night,
                timeZone = TimeZone.UTC,
                streakBefore = 6,
                unlockedTalents = talents,
            )
        )
        
        // streakBefore = 6. Status is SUCCESS (from prev test logic). streakAfter = 7.
        // D1: deltaStart = 5 mins <= 10. +5 XP.
        // D2: deltaStart = 5 mins <= 15. +10 XP.
        // D3: score = 93 >= 90. +25 XP.
        // Total Additions = 40.
        assertEquals(40, result.xpBreakdown.talentAdditionsXp)
        
        // Streak Multiplier for streak 7 is 1.2.
        assertEquals(1.2, result.xpBreakdown.streakMultiplier)
        
        // Talent Multipliers:
        // S1: streak 7 >= 3. -> 1.05.
        // S3: streak 7 >= 7. -> 1.10.
        // Total mult = 1.05 * 1.10 = 1.155.
        assertEquals(1.155, result.xpBreakdown.talentMultiplier, 0.0001)
        
        // Base XP: 100 (Success)
        // Score Bonus: (93/10)*2 = 9*2 = 18.
        // Perfect Bonus (>=90): 25.
        // Talent Additions: 40.
        // Raw XP = 100 + 18 + 25 + 40 = 183.
        // Total XP = floor(183 * 1.2 * 1.155) = floor(253.638) = 253.
        assertEquals(253, result.xpBreakdown.totalXp)
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

    private fun createTalent(id: String, effect: TalentEffect): Talent {
        return Talent(
            id = id,
            branch = TalentBranch.DISCIPLINE,
            tier = TalentTier.TIER_1,
            nameKey = "key",
            descriptionKey = "desc",
            costPoints = 1,
            effect = effect,
            isActive = true
        )
    }
}
