package com.example.domain.repository

interface TransactionRunner {
    suspend fun <T> run(block: suspend () -> T): T
}
