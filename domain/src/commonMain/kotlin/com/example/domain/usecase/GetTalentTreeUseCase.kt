package com.example.domain.usecase

import com.example.domain.model.TalentTree
import com.example.domain.repository.TalentRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult

class GetTalentTreeUseCase(
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
) {
    suspend fun execute(): AppResult<TalentTree> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val talentsResult = talentRepository.getAllTalents()
        if (talentsResult is AppResult.Error) {
            return talentsResult
        }
        val talents = (talentsResult as AppResult.Success).value
        val userTalentsResult = talentRepository.getUserTalents(user.id)
        if (userTalentsResult is AppResult.Error) {
            return userTalentsResult
        }
        val userTalents = (userTalentsResult as AppResult.Success).value
        return AppResult.Success(
            TalentTree(
                availablePoints = user.talentPointsAvailable,
                talents = talents,
                unlockedTalentIds = userTalents.map { it.talentId }.toSet(),
            )
        )
    }
}
