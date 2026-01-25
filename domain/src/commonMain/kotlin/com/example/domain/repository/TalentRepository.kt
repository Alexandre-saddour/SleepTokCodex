package com.example.domain.repository

import com.example.domain.model.Talent
import com.example.domain.model.UserTalent
import com.example.domain.result.AppResult
import kotlinx.datetime.Instant

interface TalentRepository {
    suspend fun getAllTalents(): AppResult<List<Talent>>
    suspend fun getUserTalents(userId: Long): AppResult<List<UserTalent>>
    suspend fun unlockTalent(userId: Long, talentId: String, unlockedAt: Instant): AppResult<Unit>
}
