package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.NightEntity
import com.example.domain.model.NightStatus
import kotlin.time.Instant

@Dao
interface NightDao {
    @Query("SELECT * FROM nights WHERE userId = :userId AND status = :status LIMIT 1")
    suspend fun getActiveNight(userId: Long, status: NightStatus = NightStatus.IN_PROGRESS): NightEntity?

    @Query("SELECT * FROM nights WHERE id = :nightId LIMIT 1")
    suspend fun getNightById(nightId: Long): NightEntity?

    @Query("SELECT * FROM nights WHERE userId = :userId AND startAt >= :startAt AND startAt <= :endAt ORDER BY startAt")
    suspend fun getNightsBetween(userId: Long, startAt: Instant, endAt: Instant): List<NightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(night: NightEntity): Long

    @Update
    suspend fun update(night: NightEntity)
}
