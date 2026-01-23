package com.example.domain.repository

interface WelcomeMessageRepository {

    suspend fun get(): String
}
