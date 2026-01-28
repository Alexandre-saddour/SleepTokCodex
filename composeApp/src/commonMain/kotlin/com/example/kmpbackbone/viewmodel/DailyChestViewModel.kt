package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Reward
import com.example.domain.result.DomainError
import com.example.domain.result.DomainException
import com.example.domain.result.getOrThrow
import com.example.domain.usecase.ClaimDailyChestUseCase
import com.example.domain.usecase.GetBadgesAndCosmeticsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DailyChestUiEvent {
    data object Claimed : DailyChestUiEvent
}

data class DailyChestUiState(
    val isClaiming: Boolean = false,
    val claimedReward: Reward? = null,
    val error: DomainError? = null,
) : UiState

class DailyChestViewModel(
    private val claimDailyChestUseCase: ClaimDailyChestUseCase,
    private val getBadgesAndCosmeticsUseCase: GetBadgesAndCosmeticsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DailyChestUiState())
    val state: StateFlow<DailyChestUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DailyChestUiEvent>()
    val events: SharedFlow<DailyChestUiEvent> = _events.asSharedFlow()

    fun claimChest() {
        viewModelScope.launch {
            _state.update { it.copy(isClaiming = true, error = null) }

            try {
                val userReward = claimDailyChestUseCase.execute().getOrThrow()
                val badgesAndCosmetics = getBadgesAndCosmeticsUseCase.execute().getOrThrow()
                val claimedReward = badgesAndCosmetics.rewards.firstOrNull { it.id == userReward.rewardId }

                _state.update {
                    it.copy(
                        isClaiming = false,
                        claimedReward = claimedReward,
                    )
                }
            } catch (e: DomainException) {
                _state.update { it.copy(isClaiming = false, error = e.error) }
            }
        }
    }

    fun onDismiss() {
        viewModelScope.launch {
            _events.emit(DailyChestUiEvent.Claimed)
        }
    }
}
