package com.example.domain.usecase

import com.example.domain.model.ShieldSource
import com.example.domain.model.StreakShield
import com.example.domain.model.TalentEffect
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.repository.TalentRepository
import com.example.domain.repository.UserRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import kotlin.math.max
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class UnlockTalentUseCase(
    private val userRepository: UserRepository,
    private val talentRepository: TalentRepository,
    private val streakShieldRepository: StreakShieldRepository,
) {
    suspend fun execute(talentId: String): AppResult<Unit> {
        return try {
            val user = userRepository.getActiveUser().getOrThrow()
            val allTalents = talentRepository.getAllTalents().getOrThrow()
            val talent = allTalents.firstOrNull { it.id == talentId }
                ?: return AppResult.Error(DomainError.NotFound)

            val userTalents = talentRepository.getUserTalents(user.id).getOrThrow()
            val userTalentIds = userTalents.map { it.talentId }.toSet()

            if (userTalentIds.contains(talentId)) {
                return AppResult.Error(DomainError.Conflict)
            }
            if (user.talentPointsAvailable < talent.costPoints) {
                return AppResult.Error(DomainError.Validation)
            }

            talentRepository.unlockTalent(user.id, talentId, Clock.System.now()).getOrThrow()
            applyTalentEffect(user.id, talent.effect).getOrThrow()

            val remainingPoints = user.talentPointsAvailable - talent.costPoints
            userRepository.updateXp(
                userId = user.id,
                xpTotal = user.xpTotal,
                level = user.level,
                talentPointsAvailable = remainingPoints,
            )
        } catch (e: DomainException) {
            AppResult.Error(e.error)
        }
    }

    private suspend fun applyTalentEffect(userId: Long, effect: TalentEffect): AppResult<Unit> {
        return when (effect) {
            is TalentEffect.StreakShield -> {
                try {
                    val now = Clock.System.now()
                    val existing = streakShieldRepository.getStreakShield(userId).getOrThrow()
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
                } catch (e: DomainException) {
                    AppResult.Error(e.error)
                }
            }
            else -> AppResult.Success(Unit)
        }
    }
}