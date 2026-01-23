package com.example.data.repository

import com.example.data.local.dao.SleepPlanDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.SleepPlan
import com.example.domain.repository.SleepPlanRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError

class SleepPlanRepositoryImpl(
    private val sleepPlanDao: SleepPlanDao,
) : SleepPlanRepository {
    override suspend fun getActivePlan(userId: Long): AppResult<SleepPlan?> {
        return try {
            val plan = sleepPlanDao.getActivePlan(userId)
            AppResult.Success(plan?.toDomain())
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun createPlan(plan: SleepPlan): AppResult<SleepPlan> {
        return try {
            val id = sleepPlanDao.insert(plan.toEntity())
            AppResult.Success(plan.copy(id = id))
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun updatePlan(plan: SleepPlan): AppResult<Unit> {
        return try {
            sleepPlanDao.update(plan.toEntity())
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }

    override suspend fun setActivePlan(userId: Long, planId: Long): AppResult<Unit> {
        return try {
            sleepPlanDao.setActivePlan(userId, planId)
            AppResult.Success(Unit)
        } catch (exception: Exception) {
            AppResult.Error(DomainError.Storage)
        }
    }
}
