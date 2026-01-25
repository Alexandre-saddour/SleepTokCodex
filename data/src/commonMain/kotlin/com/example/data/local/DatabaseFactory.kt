package com.example.data.local

expect class DatabaseFactory {
    constructor(context: DatabaseContext)
    fun create(): SleepTokDatabase
}
