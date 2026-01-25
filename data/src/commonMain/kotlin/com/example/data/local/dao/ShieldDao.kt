package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.StreakShieldEntity

@Dao
interface ShieldDao {
    @Query("SELECT * FROM streak_shields WHERE userId = :userId LIMIT 1")
    suspend fun getStreakShield(userId: Long): StreakShieldEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(streakShield: StreakShieldEntity)

    @Query("UPDATE streak_shields SET chargesAvailable = chargesAvailable - 1 WHERE userId = :userId AND chargesAvailable > 0")
    suspend fun consumeCharge(userId: Long)
}
