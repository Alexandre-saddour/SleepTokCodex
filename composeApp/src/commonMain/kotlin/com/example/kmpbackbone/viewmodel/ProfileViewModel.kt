package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ProfileSummary
import com.example.domain.model.Reward
import com.example.domain.model.RewardType
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.scoring.LevelCalculator
import com.example.domain.usecase.GetBadgesAndCosmeticsUseCase
import com.example.domain.usecase.GetHomeSummaryUseCase
import com.example.domain.usecase.GetProfileSummaryUseCase
import com.example.kmpbackbone.util.parseTimeZone
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileStatsUi(
    val level: Int,
    val xpInLevel: Int,
    val levelSpan: Int,
    val xpTotal: Long,
    val totalNights: Int,
    val totalWins: Int,
    val bestStreak: Int,
)

data class ProfileBadgeUi(
    val rewardId: String,
    val nameKey: String,
    val assetRef: String?,
    val isUnlocked: Boolean,
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val stats: ProfileStatsUi? = null,
    val badges: List<ProfileBadgeUi> = emptyList(),
    val error: DomainError? = null,
) : UiState

class ProfileViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val getProfileSummaryUseCase: GetProfileSummaryUseCase,
    private val getBadgesAndCosmeticsUseCase: GetBadgesAndCosmeticsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val badgesDeferred = async { getBadgesAndCosmeticsUseCase.execute() }
            val summary = when (val summaryResult = getHomeSummaryUseCase.execute()) {
                is AppResult.Success -> summaryResult.value
                is AppResult.Error -> {
                    badgesDeferred.cancel()
                    _state.update { it.copy(isLoading = false, error = summaryResult.error) }
                    return@launch
                }
            }
            val timeZone = parseTimeZone(summary.user.timezone)

            val profile = when (val profileResult = getProfileSummaryUseCase.execute(timeZone)) {
                is AppResult.Success -> profileResult.value
                is AppResult.Error -> {
                    badgesDeferred.cancel()
                    _state.update { it.copy(isLoading = false, error = profileResult.error) }
                    return@launch
                }
            }

            val badgesAndCosmetics = when (val badgesResult = badgesDeferred.await()) {
                is AppResult.Success -> badgesResult.value
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = badgesResult.error) }
                    return@launch
                }
            }
            val unlockedIds = badgesAndCosmetics.userRewards.map { it.rewardId }.toSet()
            val badges = badgesAndCosmetics.rewards
                .filter { it.type == RewardType.BADGE }
                .map { reward -> reward.toBadgeUi(unlockedIds) }

            _state.update {
                it.copy(
                    isLoading = false,
                    stats = profile.toStatsUi(),
                    badges = badges,
                )
            }
        }
    }

    private fun ProfileSummary.toStatsUi(): ProfileStatsUi {
        val progress = LevelCalculator.levelProgress(user.xpTotal)
        return ProfileStatsUi(
            level = progress.level,
            xpInLevel = progress.xpInLevel,
            levelSpan = progress.levelSpan,
            xpTotal = user.xpTotal,
            totalNights = totalNights,
            totalWins = totalWins,
            bestStreak = bestStreak,
        )
    }

    private fun Reward.toBadgeUi(unlockedIds: Set<String>): ProfileBadgeUi {
        return ProfileBadgeUi(
            rewardId = id,
            nameKey = nameKey,
            assetRef = assetRef,
            isUnlocked = unlockedIds.contains(id),
        )
    }
}
