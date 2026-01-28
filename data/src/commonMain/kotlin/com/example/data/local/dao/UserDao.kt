package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import com.example.domain.model.CoachStyle
import com.example.domain.model.PremiumStatus
import kotlin.time.Instant

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("UPDATE users SET coachStyle = :coachStyle WHERE id = :userId")
    suspend fun updateCoachStyle(userId: Long, coachStyle: CoachStyle)

    @Query("UPDATE users SET premiumStatus = :status, premiumUntil = :premiumUntil WHERE id = :userId")
    suspend fun updatePremiumStatus(userId: Long, status: PremiumStatus, premiumUntil: Instant?)

    @Query("UPDATE users SET streakCurrent = :current, streakBest = :best WHERE id = :userId")
    suspend fun updateStreak(userId: Long, current: Int, best: Int)

    @Query("UPDATE users SET xpTotal = :xpTotal, level = :level, talentPointsAvailable = :talentPointsAvailable WHERE id = :userId")
    suspend fun updateXp(userId: Long, xpTotal: Long, level: Int, talentPointsAvailable: Int)
}
