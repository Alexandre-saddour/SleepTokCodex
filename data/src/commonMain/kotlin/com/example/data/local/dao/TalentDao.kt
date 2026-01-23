package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TalentEntity
import com.example.data.local.entity.UserTalentEntity

@Dao
interface TalentDao {
    @Query("SELECT * FROM talents WHERE isActive = 1")
    suspend fun getAllTalents(): List<TalentEntity>

    @Query("SELECT COUNT(*) FROM talents")
    suspend fun countTalents(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(talents: List<TalentEntity>)

    @Query("SELECT * FROM user_talents WHERE userId = :userId")
    suspend fun getUserTalents(userId: Long): List<UserTalentEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserTalent(userTalent: UserTalentEntity)
}
