package com.example.domain.usecase

import com.example.domain.repository.TalentRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlinx.datetime.Clock

class UnlockTalentUseCase(
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
) {
    suspend fun execute(talentId: String): AppResult<Unit> {
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val allTalentsResult = talentRepository.getAllTalents()
        if (allTalentsResult is AppResult.Error) {
            return allTalentsResult
        }
        val talent = (allTalentsResult as AppResult.Success).value.firstOrNull { it.id == talentId }
            ?: return AppResult.Error(DomainError.NotFound)
        val userTalentsResult = talentRepository.getUserTalents(user.id)
        if (userTalentsResult is AppResult.Error) {
            return userTalentsResult
        }
        val userTalentIds = (userTalentsResult as AppResult.Success).value.map { it.talentId }.toSet()
        if (userTalentIds.contains(talentId)) {
            return AppResult.Error(DomainError.Conflict)
        }
        if (user.talentPointsAvailable < talent.costPoints) {
            return AppResult.Error(DomainError.Validation)
        }
        val unlockResult = talentRepository.unlockTalent(user.id, talentId, Clock.System.now())
        if (unlockResult is AppResult.Error) {
            return unlockResult
        }
        val remainingPoints = user.talentPointsAvailable - talent.costPoints
        return userRepository.updateXp(
            userId = user.id,
            xpTotal = user.xpTotal,
            level = user.level,
            talentPointsAvailable = remainingPoints,
        )
    }
}
