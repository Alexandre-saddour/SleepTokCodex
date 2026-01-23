package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.NightDao
import com.example.data.local.dao.RewardDao
import com.example.data.local.dao.ShieldDao
import com.example.data.local.dao.SleepPlanDao
import com.example.data.local.dao.TalentDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.XpEventDao
import com.example.data.local.entity.NightEntity
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.SleepPlanEntity
import com.example.data.local.entity.StreakShieldEntity
import com.example.data.local.entity.TalentEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserRewardEntity
import com.example.data.local.entity.UserTalentEntity
import com.example.data.local.entity.XpEventEntity

@Database(
    entities = [
        UserEntity::class,
        SleepPlanEntity::class,
        NightEntity::class,
        XpEventEntity::class,
        TalentEntity::class,
        UserTalentEntity::class,
        RewardEntity::class,
        UserRewardEntity::class,
        StreakShieldEntity::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class SleepTokDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sleepPlanDao(): SleepPlanDao
    abstract fun nightDao(): NightDao
    abstract fun xpEventDao(): XpEventDao
    abstract fun talentDao(): TalentDao
    abstract fun rewardDao(): RewardDao
    abstract fun shieldDao(): ShieldDao
}
