package com.example.domain.usecase

import com.example.domain.model.ShieldSource
import com.example.domain.model.StreakShield
import com.example.domain.model.TalentEffect
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.repository.TalentRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import kotlin.math.max
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class UnlockTalentUseCase(
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
    private val streakShieldRepository: StreakShieldRepository,
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
        val effectResult = applyTalentEffect(user.id, talent.effect)
        if (effectResult is AppResult.Error) {
            return effectResult
        }
        val remainingPoints = user.talentPointsAvailable - talent.costPoints
        return userRepository.updateXp(
            userId = user.id,
            xpTotal = user.xpTotal,
            level = user.level,
            talentPointsAvailable = remainingPoints,
        )
    }

    private suspend fun applyTalentEffect(userId: Long, effect: TalentEffect): AppResult<Unit> {
        return when (effect) {
            is TalentEffect.StreakShield -> {
                val now = Clock.System.now()
                val existingResult = streakShieldRepository.getStreakShield(userId)
                if (existingResult is AppResult.Error) {
                    return existingResult
                }
                val existing = existingResult.value
                val refreshAt = existing?.refreshAt ?: now.plus(7, DateTimeUnit.DAY, TimeZone.UTC)
                val charges = max(existing?.chargesAvailable ?: 0, effect.chargesPerWeek)
                streakShieldRepository.upsertStreakShield(
                    StreakShield(
                        userId = userId,
                        chargesAvailable = charges,
                        refreshAt = refreshAt,
                        source = existing?.source ?: ShieldSource.TALENT,
                    )
                )
            }
            else -> AppResult.Success(Unit)
        }
    }
}
