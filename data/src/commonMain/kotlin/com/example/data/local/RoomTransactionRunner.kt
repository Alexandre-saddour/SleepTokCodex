package com.example.data.local

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.example.domain.repository.TransactionRunner

class RoomTransactionRunner(
    private val database: SleepTokDatabase
) : TransactionRunner {
    override suspend fun <T> run(block: suspend () -> T): T {
        return database.useWriterConnection { transactor ->
            transactor.immediateTransaction { block() }
        }
    }
}
