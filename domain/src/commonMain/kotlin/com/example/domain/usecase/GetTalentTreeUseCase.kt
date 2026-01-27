package com.example.domain.usecase

import com.example.domain.model.TalentTree
import com.example.domain.repository.TalentRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow

class GetTalentTreeUseCase(
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
) {
    suspend fun execute(): AppResult<TalentTree> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val talents = talentRepository.getAllTalents().getOrThrow()
            val userTalents = talentRepository.getUserTalents(user.id).getOrThrow()

            AppResult.Success(
                TalentTree(
                    availablePoints = user.talentPointsAvailable,
                    talents = talents,
                    unlockedTalentIds = userTalents.map { it.talentId }.toSet(),
                )
            )
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }
}
