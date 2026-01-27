package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.model.XpEvent
import com.example.domain.model.XpEventType
import com.example.domain.model.NightResult
import com.example.domain.result.RollbackException
import com.example.domain.result.getOrRollback
import com.example.domain.repository.NightRepository
import com.example.domain.repository.StreakShieldRepository
import com.example.domain.repository.TransactionRunner
import com.example.domain.repository.UserRepository
import com.example.domain.repository.XpEventRepository
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.scoring.LevelCalculator
import kotlin.math.max
import kotlinx.datetime.Clock

class ApplyNightResultUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
    private val xpEventRepository: XpEventRepository,
    private val transactionRunner: TransactionRunner,
    private val streakShieldRepository: StreakShieldRepository,
) {
    suspend fun execute(
        night: Night,
        result: NightResult,
        consumeShield: Boolean = false,
    ): AppResult<Unit> {
        return try {
            transactionRunner.run {
                val updatedNight = night.copy(
                    status = result.status,
                    score = result.score,
                    xpEarned = result.xpBreakdown.totalXp,
                    streakAfter = result.streakAfter,
                    actualDurationMinutes = result.actualDurationMinutes,
                )
                nightRepository.updateNight(updatedNight).getOrRollback()
                val user = userRepository.getActiveUser().getOrRollback()
                if (consumeShield) {
                    val shield = streakShieldRepository.getStreakShield(user.id).getOrRollback()
                    val available = shield?.chargesAvailable ?: 0
                    if (available <= 0) {
                        throw RollbackException(AppResult.Error(DomainError.Validation))
                    }
                    streakShieldRepository.consumeCharge(user.id).getOrRollback()
                }
                val newXpTotal = user.xpTotal + result.xpBreakdown.totalXp
                val levelUpdate = LevelCalculator.applyLevelUpdate(
                    currentLevel = user.level,
                    currentTalentPoints = user.talentPointsAvailable,
                    xpTotal = newXpTotal,
                )
                val newBestStreak = max(user.streakBest, result.streakAfter)
                userRepository.updateXp(
                    userId = user.id,
                    xpTotal = newXpTotal,
                    level = levelUpdate.level,
                    talentPointsAvailable = levelUpdate.talentPointsAvailable,
                ).getOrRollback()
                userRepository.updateStreak(
                    userId = user.id,
                    current = result.streakAfter,
                    best = newBestStreak,
                ).getOrRollback()
                val xpEvent = XpEvent(
                    id = 0L,
                    userId = user.id,
                    nightId = night.id,
                    type = result.status.toXpEventType(),
                    amount = result.xpBreakdown.totalXp,
                    createdAt = Clock.System.now(),
                    metaJson = null,
                )
                xpEventRepository.addXpEvent(xpEvent).getOrRollback()
                AppResult.Success(Unit)
            }
        } catch (e: RollbackException) {
            e.error
        }
    }

    private fun NightStatus.toXpEventType(): XpEventType {
        return when (this) {
            NightStatus.SUCCESS -> XpEventType.NIGHT_SUCCESS
            NightStatus.PARTIAL -> XpEventType.NIGHT_PARTIAL
            NightStatus.FAIL -> XpEventType.NIGHT_FAIL
            NightStatus.IN_PROGRESS -> XpEventType.OTHER
            NightStatus.VOID -> XpEventType.OTHER
        }
    }
}
