package com.example.data.repository

import com.example.data.local.dao.TalentDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Talent
import com.example.domain.model.UserTalent
import com.example.domain.repository.TalentRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Instant

class TalentRepositoryImpl(
    private val talentDao: TalentDao,
) : TalentRepository {
    override suspend fun getAllTalents(): AppResult<List<Talent>> {
        return try {
            AppResult.Success(talentDao.getAllTalents().map { it.toDomain() })
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun getUserTalents(userId: Long): AppResult<List<UserTalent>> {
        return try {
            AppResult.Success(talentDao.getUserTalents(userId).map { it.toDomain() })
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun unlockTalent(userId: Long, talentId: String, unlockedAt: Instant): AppResult<Unit> {
        return try {
            talentDao.insertUserTalent(UserTalent(userId, talentId, unlockedAt).toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
