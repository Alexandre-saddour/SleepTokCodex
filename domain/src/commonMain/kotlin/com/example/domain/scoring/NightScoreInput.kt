package com.example.domain.scoring

import com.example.domain.model.Night
import com.example.domain.model.SleepPlan
import com.example.domain.model.Talent
import kotlinx.datetime.TimeZone

data class NightScoreInput(
    val plan: SleepPlan,
    val night: Night,
    val timeZone: TimeZone,
    val streakBefore: Int,
    val unlockedTalents: List<Talent>,
)
