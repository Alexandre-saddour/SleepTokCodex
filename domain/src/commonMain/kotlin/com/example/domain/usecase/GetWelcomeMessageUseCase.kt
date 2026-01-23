package com.example.domain.usecase

import com.example.domain.repository.WelcomeMessageRepository

class GetWelcomeMessageUseCase(private val welcomeMessageRepository: WelcomeMessageRepository) {

    suspend fun execute() = welcomeMessageRepository.get()
}