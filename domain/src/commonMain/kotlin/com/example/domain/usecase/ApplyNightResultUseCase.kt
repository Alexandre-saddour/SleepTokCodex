package com.example.domain.usecase

import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.model.XpEvent
import com.example.domain.model.XpEventType
import com.example.domain.model.NightResult
import com.example.domain.repository.NightRepository
import com.example.domain.repository.UserRepository
import com.example.domain.repository.XpEventRepository
import com.example.domain.result.AppResult
import com.example.domain.scoring.LevelCalculator
import kotlinx.datetime.Clock

class ApplyNightResultUseCase(
    private val userRepository: UserRepository,
    private val nightRepository: NightRepository,
    private val xpEventRepository: XpEventRepository,
) {
    suspend fun execute(night: Night, result: NightResult): AppResult<Unit> {
        val updatedNight = night.copy(
            status = result.status,
            score = result.score,
            xpEarned = result.xpBreakdown.totalXp,
            streakAfter = result.streakAfter,
            actualDurationMinutes = result.actualDurationMinutes,
        )
        val nightUpdateResult = nightRepository.updateNight(updatedNight)
        if (nightUpdateResult is AppResult.Error) {
            return nightUpdateResult
        }
        val userResult = userRepository.getActiveUser()
        if (userResult is AppResult.Error) {
            return userResult
        }
        val user = (userResult as AppResult.Success).value
        val newXpTotal = user.xpTotal + result.xpBreakdown.totalXp
        val levelUpdate = LevelCalculator.applyLevelUpdate(
            currentLevel = user.level,
            currentTalentPoints = user.talentPointsAvailable,
            xpTotal = newXpTotal,
        )
        val newBestStreak = maxOf(user.streakBest, result.streakAfter)
        val xpUpdateResult = userRepository.updateXp(
            userId = user.id,
            xpTotal = newXpTotal,
            level = levelUpdate.level,
            talentPointsAvailable = levelUpdate.talentPointsAvailable,
        )
        if (xpUpdateResult is AppResult.Error) {
            return xpUpdateResult
        }
        val streakUpdateResult = userRepository.updateStreak(
            userId = user.id,
            current = result.streakAfter,
            best = newBestStreak,
        )
        if (streakUpdateResult is AppResult.Error) {
            return streakUpdateResult
        }
        val xpEvent = XpEvent(
            id = 0L,
            userId = user.id,
            nightId = night.id,
            type = result.status.toXpEventType(),
            amount = result.xpBreakdown.totalXp,
            createdAt = Clock.System.now(),
            metaJson = null,
        )
        return xpEventRepository.addXpEvent(xpEvent)
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
