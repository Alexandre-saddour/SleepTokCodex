package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SleepPlanEntity

@Dao
interface SleepPlanDao {
    @Query("SELECT * FROM sleep_plans WHERE userId = :userId AND isActive = 1 LIMIT 1")
    suspend fun getActivePlan(userId: Long): SleepPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: SleepPlanEntity): Long

    @Update
    suspend fun update(plan: SleepPlanEntity)

    @Query("UPDATE sleep_plans SET isActive = CASE WHEN id = :planId THEN 1 ELSE 0 END WHERE userId = :userId")
    suspend fun setActivePlan(userId: Long, planId: Long)
}
