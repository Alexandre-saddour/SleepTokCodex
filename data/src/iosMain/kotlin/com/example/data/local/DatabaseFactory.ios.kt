package com.example.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseFactory actual constructor(
    private val context: DatabaseContext,
) {
    actual fun create(): SleepTokDatabase {
        return Room.databaseBuilder<SleepTokDatabase>(
            DatabaseConfig.DATABASE_NAME,
        ).setDriver(BundledSQLiteDriver()).build()
    }
}
