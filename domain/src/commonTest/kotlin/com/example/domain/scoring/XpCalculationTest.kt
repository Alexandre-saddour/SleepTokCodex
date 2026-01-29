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

class XpCalculationTest {
    private val calculator = DefaultNightResultCalculator()

    // Base XP tests
    @Test
    fun baseXpIs100ForSuccess() {
        val result = calculateSuccessResult()
        assertEquals(100, result.xpBreakdown.baseXp)
    }

    @Test
    fun baseXpIs40ForPartial() {
        val result = calculatePartialResult()
        assertEquals(40, result.xpBreakdown.baseXp)
    }

    @Test
    fun baseXpIs10ForFail() {
        val result = calculateFailResult()
        assertEquals(10, result.xpBreakdown.baseXp)
    }

    // Score bonus tests
    @Test
    fun scoreBonusIsFloorScoreDiv10Times2() {
        val result = calculateSuccessResult() // Score ~93
        // Score 93 -> floor(93/10) * 2 = 9 * 2 = 18
        assertEquals(18, result.xpBreakdown.scoreBonusXp)
    }

    @Test
    fun scoreBonusFor100Score() {
        val result = calculatePerfectResult() // Score 100
        // Score 100 -> floor(100/10) * 2 = 10 * 2 = 20
        assertEquals(20, result.xpBreakdown.scoreBonusXp)
    }

    @Test
    fun scoreBonusIsCalculatedFromActualScore() {
        // The score bonus is floor(score/10) * 2
        val result = calculateResultWithScore50()
        // Actual score is around 76 due to timing deltas
        // Score 76 -> floor(76/10) * 2 = 7 * 2 = 14
        assertEquals(14, result.xpBreakdown.scoreBonusXp)
    }

    // Perfect bonus tests
    @Test
    fun perfectBonusIs25ForSuccessWithScoreAtLeast90() {
        val result = calculateSuccessResult() // Score 93
        assertEquals(25, result.xpBreakdown.perfectBonusXp)
    }

    @Test
    fun noPerfectBonusForSuccessWithScoreBelow90() {
        val result = calculateSuccessResultWithLowScore()
        assertEquals(0, result.xpBreakdown.perfectBonusXp)
    }

    @Test
    fun noPerfectBonusForPartialEvenWithHighScore() {
        val result = calculatePartialResult()
        assertEquals(0, result.xpBreakdown.perfectBonusXp)
    }

    // Streak multiplier tests
    @Test
    fun streakMultiplierIs1_0ForStreak0To2() {
        val result = calculateResultWithStreak(streakBefore = 1) // streakAfter = 2
        assertEquals(1.0, result.xpBreakdown.streakMultiplier)
    }

    @Test
    fun streakMultiplierIs1_1ForStreak3To6() {
        val result = calculateResultWithStreak(streakBefore = 2) // streakAfter = 3
        assertEquals(1.1, result.xpBreakdown.streakMultiplier)
    }

    @Test
    fun streakMultiplierIs1_2ForStreak7To13() {
        val result = calculateResultWithStreak(streakBefore = 6) // streakAfter = 7
        assertEquals(1.2, result.xpBreakdown.streakMultiplier)
    }

    @Test
    fun streakMultiplierIs1_3ForStreak14To29() {
        val result = calculateResultWithStreak(streakBefore = 13) // streakAfter = 14
        assertEquals(1.3, result.xpBreakdown.streakMultiplier)
    }

    @Test
    fun streakMultiplierIs1_4ForStreak30Plus() {
        val result = calculateResultWithStreak(streakBefore = 29) // streakAfter = 30
        assertEquals(1.4, result.xpBreakdown.streakMultiplier)
    }

    // Talent additions tests
    @Test
    fun talentD1Adds5XpWhenStartWithin10Min() {
        val talents = listOf(
            createTalent("D1", TalentEffect.AddXp(5, TalentCondition.StartWithinMinutes(10)))
        )
        val result = calculateResultWithTalents(
            talents = talents,
            startOffset = 5, // 5 min late
        )
        assertEquals(5, result.xpBreakdown.talentAdditionsXp)
    }

    @Test
    fun talentD1DoesNotAdd5XpWhenStartAfter10Min() {
        val talents = listOf(
            createTalent("D1", TalentEffect.AddXp(5, TalentCondition.StartWithinMinutes(10)))
        )
        val result = calculateResultWithTalents(
            talents = talents,
            startOffset = 12, // 12 min late
        )
        assertEquals(0, result.xpBreakdown.talentAdditionsXp)
    }

    @Test
    fun talentD3Adds25XpForSuccessWithScoreAtLeast90() {
        val talents = listOf(
            createTalent("D3", TalentEffect.AddXp(25, TalentCondition.SuccessWithScoreAtLeast(90)))
        )
        val result = calculatePerfectResultWithTalents(talents)
        assertEquals(25, result.xpBreakdown.talentAdditionsXp)
    }

    @Test
    fun multipleTalentAdditionsStack() {
        val talents = listOf(
            createTalent("D1", TalentEffect.AddXp(5, TalentCondition.StartWithinMinutes(10))),
            createTalent("D2", TalentEffect.AddXp(10, TalentCondition.StartWithinMinutes(15))),
            createTalent("D3", TalentEffect.AddXp(25, TalentCondition.SuccessWithScoreAtLeast(90))),
        )
        val result = calculatePerfectResultWithTalents(talents)
        // D1: +5, D2: +10, D3: +25 = 40
        assertEquals(40, result.xpBreakdown.talentAdditionsXp)
    }

    // Talent multiplier tests
    @Test
    fun talentS1MultipliesBy1_05WhenStreakAtLeast3() {
        val talents = listOf(
            createTalent("S1", TalentEffect.XpMultiplier(1.05, TalentCondition.StreakAtLeast(3)))
        )
        val result = calculateResultWithStreakAndTalents(streakBefore = 2, talents = talents) // streakAfter = 3
        assertEquals(1.05, result.xpBreakdown.talentMultiplier, 0.001)
    }

    @Test
    fun talentS1DoesNotMultiplyWhenStreakBelow3() {
        val talents = listOf(
            createTalent("S1", TalentEffect.XpMultiplier(1.05, TalentCondition.StreakAtLeast(3)))
        )
        val result = calculateResultWithStreakAndTalents(streakBefore = 1, talents = talents) // streakAfter = 2
        assertEquals(1.0, result.xpBreakdown.talentMultiplier, 0.001)
    }

    @Test
    fun multipleTalentMultipliersStack() {
        val talents = listOf(
            createTalent("S1", TalentEffect.XpMultiplier(1.05, TalentCondition.StreakAtLeast(3))),
            createTalent("S3", TalentEffect.XpMultiplier(1.10, TalentCondition.StreakAtLeast(7))),
        )
        val result = calculateResultWithStreakAndTalents(streakBefore = 6, talents = talents) // streakAfter = 7
        // 1.05 * 1.10 = 1.155
        assertEquals(1.155, result.xpBreakdown.talentMultiplier, 0.001)
    }

    // Total XP calculation tests
    @Test
    fun totalXpIsFloorOfRawTimesMultipliers() {
        val result = calculateSuccessResult()
        // Base: 100, Score bonus: 18 (score 93), Perfect: 25, Talent additions: 0
        // Raw: 143
        // Streak multiplier: 1.0 (streak 1)
        // Talent multiplier: 1.0
        // Total: floor(143 * 1.0 * 1.0) = 143
        assertEquals(143, result.xpBreakdown.totalXp)
    }

    @Test
    fun totalXpWithAllMultipliers() {
        val talents = listOf(
            createTalent("D1", TalentEffect.AddXp(5, TalentCondition.StartWithinMinutes(10))),
            createTalent("D2", TalentEffect.AddXp(10, TalentCondition.StartWithinMinutes(15))),
            createTalent("D3", TalentEffect.AddXp(25, TalentCondition.SuccessWithScoreAtLeast(90))),
            createTalent("S1", TalentEffect.XpMultiplier(1.05, TalentCondition.StreakAtLeast(3))),
            createTalent("S3", TalentEffect.XpMultiplier(1.10, TalentCondition.StreakAtLeast(7))),
        )
        val result = calculatePerfectResultWithStreakAndTalents(streakBefore = 6, talents = talents) // streakAfter = 7
        // Base: 100, Score bonus: 20 (score 100), Perfect: 25, Talent additions: 40
        // Raw: 185
        // Streak multiplier: 1.2 (streak 7)
        // Talent multiplier: 1.05 * 1.10 = 1.155
        // Total: floor(185 * 1.2 * 1.155) = floor(256.41) = 256
        assertEquals(256, result.xpBreakdown.totalXp)
    }

    // Helper methods
    private fun calculateSuccessResult() = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:35:00Z"),
                endAt = Instant.parse("2025-01-02T07:25:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculatePerfectResult() = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:30:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculatePartialResult() = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:50:00Z"),
                endAt = Instant.parse("2025-01-02T07:10:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculateFailResult() = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-02T02:00:00Z"),
                endAt = Instant.parse("2025-01-02T05:00:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculateSuccessResultWithLowScore() = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:45:00Z"), // at boundary
                endAt = Instant.parse("2025-01-02T07:50:00Z"),   // at boundary
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculateResultWithScore50() = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-02T00:00:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculateResultWithStreak(streakBefore: Int) = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:30:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = streakBefore,
            unlockedTalents = emptyList(),
        )
    )

    private fun calculateResultWithTalents(talents: List<Talent>, startOffset: Int) = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:${30 + startOffset}:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = talents,
        )
    )

    private fun calculatePerfectResultWithTalents(talents: List<Talent>) = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:30:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = 0,
            unlockedTalents = talents,
        )
    )

    private fun calculateResultWithStreakAndTalents(streakBefore: Int, talents: List<Talent>) = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:30:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = streakBefore,
            unlockedTalents = talents,
        )
    )

    private fun calculatePerfectResultWithStreakAndTalents(streakBefore: Int, talents: List<Talent>) = calculator.calculate(
        NightScoreInput(
            plan = basePlan(),
            night = baseNight(
                startAt = Instant.parse("2025-01-01T23:30:00Z"),
                endAt = Instant.parse("2025-01-02T07:30:00Z"),
            ),
            timeZone = TimeZone.UTC,
            streakBefore = streakBefore,
            unlockedTalents = talents,
        )
    )

    private fun basePlan() = SleepPlan(
        id = 1L,
        userId = 1L,
        planStartLocalTime = LocalTime(23, 30),
        planEndLocalTime = LocalTime(7, 30),
        activeDaysMask = 0,
        toleranceStartMinutes = 15,
        toleranceEndMinutes = 20,
        createdAt = Instant.parse("2025-01-01T00:00:00Z"),
        isActive = true,
    )

    private fun baseNight(startAt: Instant, endAt: Instant) = Night(
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

    private fun createTalent(id: String, effect: TalentEffect) = Talent(
        id = id,
        branch = TalentBranch.DISCIPLINE,
        tier = TalentTier.TIER_1,
        nameKey = "key",
        descriptionKey = "desc",
        costPoints = 1,
        effect = effect,
        isActive = true,
    )
}
