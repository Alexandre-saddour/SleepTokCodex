package com.example.data.local

import androidx.room.Room

actual class DatabaseFactory actual constructor(
    private val context: DatabaseContext,
) {
    actual fun create(): SleepTokDatabase {
        return Room.databaseBuilder(
            context,
            SleepTokDatabase::class.java,
            DatabaseConfig.DATABASE_NAME,
        ).build()
    }
}
