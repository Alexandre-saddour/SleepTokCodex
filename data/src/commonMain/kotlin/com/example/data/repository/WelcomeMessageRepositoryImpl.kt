package com.example.data.repository

import com.example.domain.repository.WelcomeMessageRepository
import com.example.data.getPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class WelcomeMessageRepositoryImpl : WelcomeMessageRepository {
    override suspend fun get(): String = withContext(Dispatchers.IO) {
        "Welcome from ${getPlatform().name}"
    }
}
