package com.example.domain.repository

import com.example.domain.model.SleepPlan
import com.example.domain.result.AppResult

interface SleepPlanRepository {
    suspend fun getActivePlan(userId: Long): AppResult<SleepPlan?>
    suspend fun createPlan(plan: SleepPlan): AppResult<SleepPlan>
    suspend fun updatePlan(plan: SleepPlan): AppResult<Unit>
    suspend fun setActivePlan(userId: Long, planId: Long): AppResult<Unit>
}
