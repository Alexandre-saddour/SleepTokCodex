package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CoachStyle
import com.example.domain.model.Night
import com.example.domain.model.NightResult
import com.example.domain.model.NightStatus
import com.example.domain.model.Talent
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.scoring.NightScoreInput
import com.example.domain.usecase.ApplyNightResultUseCase
import com.example.domain.usecase.ComputeNightResultUseCase
import com.example.domain.usecase.GetHomeSummaryUseCase
import com.example.domain.usecase.GetNightDetailUseCase
import com.example.domain.usecase.GetStreakShieldUseCase
import com.example.domain.usecase.GetTalentTreeUseCase
import com.example.kmpbackbone.util.parseTimeZone
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NightResultUiState(
    val isLoading: Boolean = true,
    val isApplying: Boolean = false,
    val nightId: Long? = null,
    val night: Night? = null,
    val result: NightResult? = null,
    val coachStyle: CoachStyle = CoachStyle.CHILL,
    val shieldAvailable: Boolean = false,
    val shieldCharges: Int = 0,
    val shieldUsed: Boolean = false,
    val isApplied: Boolean = false,
    val error: DomainError? = null,
) : UiState

sealed class NightResultUiEvent : UiEvent {
    data object NavigateBack : NightResultUiEvent()
}

class NightResultViewModel(
    private val getNightDetailUseCase: GetNightDetailUseCase,
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val getTalentTreeUseCase: GetTalentTreeUseCase,
    private val getStreakShieldUseCase: GetStreakShieldUseCase,
    private val computeNightResultUseCase: ComputeNightResultUseCase,
    private val applyNightResultUseCase: ApplyNightResultUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NightResultUiState())
    val state: StateFlow<NightResultUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<NightResultUiEvent>()
    val events = _events.asSharedFlow()

    fun load(nightId: Long) {
        val currentId = _state.value.nightId
        if (currentId == nightId && !_state.value.isLoading) {
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, nightId = nightId) }
            val nightResult = getNightDetailUseCase.execute(nightId)
            if (nightResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = nightResult.error) }
                return@launch
            }
            val night = (nightResult as AppResult.Success).value
            if (night == null) {
                _state.update { it.copy(isLoading = false, error = DomainError.NotFound) }
                return@launch
            }
            val summaryDeferred = async { getHomeSummaryUseCase.execute() }
            val talentTreeDeferred = async { getTalentTreeUseCase.execute() }
            val streakShieldDeferred = async { getStreakShieldUseCase.execute() }
            val summaryResult = summaryDeferred.await()
            if (summaryResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = summaryResult.error) }
                return@launch
            }
            val summary = (summaryResult as AppResult.Success).value
            val talentTreeResult = talentTreeDeferred.await()
            if (talentTreeResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = talentTreeResult.error) }
                return@launch
            }
            val talentTree = (talentTreeResult as AppResult.Success).value
            val unlockedTalents = unlockedTalents(talentTree.talents, talentTree.unlockedTalentIds)
            val timeZone = parseTimeZone(summary.user.timezone)
            if (night.endAt == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = DomainError.Validation,
                        night = night,
                        coachStyle = summary.user.coachStyle,
                    )
                }
                return@launch
            }
            val scoreInput = NightScoreInput(
                plan = summary.plan,
                night = night,
                timeZone = timeZone,
                streakBefore = night.streakBefore ?: summary.user.streakCurrent,
                unlockedTalents = unlockedTalents,
            )
            val computedResult = computeNightResultUseCase.execute(scoreInput)
            if (computedResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = computedResult.error) }
                return@launch
            }
            var result = (computedResult as AppResult.Success).value
            val storedStreakAfter = night.streakAfter
            if (storedStreakAfter != null && storedStreakAfter != result.streakAfter) {
                result = result.copy(streakAfter = storedStreakAfter)
            }
            val shieldResult = streakShieldDeferred.await()
            val shield = if (shieldResult is AppResult.Success) shieldResult.value else null
            val shieldAvailable = (shield?.chargesAvailable ?: 0) > 0
            val isApplied = night.status != NightStatus.IN_PROGRESS && night.xpEarned != null
            _state.update {
                it.copy(
                    isLoading = false,
                    night = night,
                    result = result,
                    coachStyle = summary.user.coachStyle,
                    shieldAvailable = shieldAvailable,
                    shieldCharges = shield?.chargesAvailable ?: 0,
                    isApplied = isApplied,
                )
            }
        }
    }

    fun onContinue() {
        viewModelScope.launch {
            applyResultFromState(
                consumeShield = false,
                shieldUsed = false,
                navigateIfApplied = true,
                resultTransform = { it },
            )
        }
    }

    fun onUseShield() {
        viewModelScope.launch {
            applyResultFromState(
                consumeShield = true,
                shieldUsed = true,
                navigateIfApplied = false,
                resultTransform = { result -> result.copy(streakAfter = result.streakBefore) },
            )
        }
    }

    private suspend fun applyResultFromState(
        consumeShield: Boolean,
        shieldUsed: Boolean,
        navigateIfApplied: Boolean,
        resultTransform: (NightResult) -> NightResult,
    ) {
        val snapshot = _state.value
        val night = snapshot.night ?: return
        val result = snapshot.result ?: return
        if (snapshot.isApplied) {
            if (navigateIfApplied) {
                emitNavigateBack()
            }
            return
        }
        val transformedResult = resultTransform(result)
        applyResult(
            night = night,
            result = transformedResult,
            consumeShield = consumeShield,
            shieldUsed = shieldUsed,
        )
    }

    private suspend fun applyResult(
        night: Night,
        result: NightResult,
        consumeShield: Boolean,
        shieldUsed: Boolean,
    ) {
        _state.update { it.copy(isApplying = true, error = null) }
        val applyResult = applyNightResultUseCase.execute(
            night = night,
            result = result,
            consumeShield = consumeShield,
        )
        when (applyResult) {
            is AppResult.Success -> {
                _state.update {
                    it.copy(
                        isApplying = false,
                        isApplied = true,
                        shieldUsed = it.shieldUsed || shieldUsed,
                        result = result,
                    )
                }
                emitNavigateBack()
            }
            is AppResult.Error -> {
                _state.update { it.copy(isApplying = false, error = applyResult.error) }
            }
        }
    }

    private fun emitNavigateBack() {
        viewModelScope.launch {
            _events.emit(NightResultUiEvent.NavigateBack)
        }
    }

    private fun unlockedTalents(all: List<Talent>, unlockedIds: Set<String>): List<Talent> {
        return all.filter { unlockedIds.contains(it.id) }
    }
}
