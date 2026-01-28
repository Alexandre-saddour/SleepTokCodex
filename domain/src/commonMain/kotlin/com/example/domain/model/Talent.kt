package com.example.domain.model

import kotlin.time.Instant

data class Talent(
    val id: String,
    val branch: TalentBranch,
    val tier: TalentTier,
    val nameKey: String,
    val descriptionKey: String,
    val costPoints: Int,
    val effect: TalentEffect,
    val isActive: Boolean,
)

data class UserTalent(
    val userId: Long,
    val talentId: String,
    val unlockedAt: Instant,
)
