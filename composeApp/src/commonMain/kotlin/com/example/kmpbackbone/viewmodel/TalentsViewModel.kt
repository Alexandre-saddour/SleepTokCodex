package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Talent
import com.example.domain.model.TalentTier
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.usecase.GetTalentTreeUseCase
import com.example.domain.usecase.UnlockTalentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TalentNodeUi(
    val talent: Talent,
    val isUnlocked: Boolean,
    val isUnlockable: Boolean,
)

data class TalentsUiState(
    val isLoading: Boolean = true,
    val isUnlocking: Boolean = false,
    val availablePoints: Int = 0,
    val talents: List<TalentNodeUi> = emptyList(),
    val error: DomainError? = null,
) : UiState

class TalentsViewModel(
    private val getTalentTreeUseCase: GetTalentTreeUseCase,
    private val unlockTalentUseCase: UnlockTalentUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(TalentsUiState())
    val state: StateFlow<TalentsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getTalentTreeUseCase.execute()) {
                is AppResult.Success -> {
                    val tree = result.value
                    val unlockedIds = tree.unlockedTalentIds
                    val talentsByBranchAndTier = tree.talents.associateBy { it.branch to it.tier }
                    val nodes = tree.talents.map { talent ->
                        val unlocked = unlockedIds.contains(talent.id)
                        val hasPrereq = hasPrerequisite(talent, talentsByBranchAndTier, unlockedIds)
                        val unlockable = !unlocked && hasPrereq && tree.availablePoints >= talent.costPoints
                        TalentNodeUi(
                            talent = talent,
                            isUnlocked = unlocked,
                            isUnlockable = unlockable,
                        )
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            availablePoints = tree.availablePoints,
                            talents = nodes,
                        )
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    fun unlockTalent(talentId: String) {
        if (_state.value.isUnlocking) return
        viewModelScope.launch {
            _state.update { it.copy(isUnlocking = true, error = null) }
            when (val result = unlockTalentUseCase.execute(talentId)) {
                is AppResult.Success -> load()
                is AppResult.Error -> _state.update { it.copy(isUnlocking = false, error = result.error) }
            }
        }
    }

    private fun hasPrerequisite(
        talent: Talent,
        talentsByBranchAndTier: Map<Pair<com.example.domain.model.TalentBranch, TalentTier>, Talent>,
        unlockedIds: Set<String>,
    ): Boolean {
        val previousTier = when (talent.tier) {
            TalentTier.TIER_1 -> null
            TalentTier.TIER_2 -> TalentTier.TIER_1
            TalentTier.TIER_3 -> TalentTier.TIER_2
        }
        val required = previousTier?.let { tier ->
            talentsByBranchAndTier[talent.branch to tier]
        }
        return required?.let { unlockedIds.contains(it.id) } ?: true
    }
}
