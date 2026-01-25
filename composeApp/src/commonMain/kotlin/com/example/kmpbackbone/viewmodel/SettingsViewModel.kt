package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CoachStyle
import com.example.domain.model.SleepPlan
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.usecase.GetHomeSummaryUseCase
import com.example.domain.usecase.UpdateCoachStyleUseCase
import com.example.domain.usecase.UpdatePlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val plan: SleepPlan? = null,
    val coachStyle: CoachStyle = CoachStyle.CHILL,
    val notificationsEnabled: Boolean = false,
    val error: DomainError? = null,
) : UiState

class SettingsViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val updatePlanUseCase: UpdatePlanUseCase,
    private val updateCoachStyleUseCase: UpdateCoachStyleUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getHomeSummaryUseCase.execute()) {
                is AppResult.Error -> _state.update {
                    it.copy(isLoading = false, error = result.error)
                }
                is AppResult.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        plan = result.value.plan,
                        coachStyle = result.value.user.coachStyle,
                    )
                }
            }
        }
    }

    fun onBedtimeChanged(time: kotlinx.datetime.LocalTime) {
        _state.update { current ->
            val plan = current.plan ?: return@update current
            current.copy(plan = plan.copy(planStartLocalTime = time))
        }
    }

    fun onWakeTimeChanged(time: kotlinx.datetime.LocalTime) {
        _state.update { current ->
            val plan = current.plan ?: return@update current
            current.copy(plan = plan.copy(planEndLocalTime = time))
        }
    }

    fun onActiveDayToggled(day: DayOfWeek) {
        _state.update { current ->
            val plan = current.plan ?: return@update current
            val activeDays = activeDaysFromMask(plan.activeDaysMask).toMutableSet()
            if (activeDays.contains(day)) {
                activeDays.remove(day)
            } else {
                activeDays.add(day)
            }
            current.copy(plan = plan.copy(activeDaysMask = activeDaysMask(activeDays)))
        }
    }

    fun onCoachStyleSelected(style: CoachStyle) {
        _state.update { it.copy(coachStyle = style) }
    }

    fun onSave() {
        viewModelScope.launch {
            val snapshot = _state.value
            val plan = snapshot.plan ?: return@launch
            _state.update { it.copy(isSaving = true, error = null) }
            val planResult = updatePlanUseCase.execute(plan)
            if (planResult is AppResult.Error) {
                _state.update { it.copy(isSaving = false, error = planResult.error) }
                return@launch
            }
            val coachResult = updateCoachStyleUseCase.execute(snapshot.coachStyle)
            if (coachResult is AppResult.Error) {
                _state.update { it.copy(isSaving = false, error = coachResult.error) }
                return@launch
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun activeDaysMask(days: Set<DayOfWeek>): Int {
        return days.fold(0) { mask, day ->
            mask or (1 shl day.ordinal)
        }
    }

    private fun activeDaysFromMask(mask: Int): Set<DayOfWeek> {
        return DayOfWeek.entries
            .filter { day -> mask and (1 shl day.ordinal) != 0 }
            .toSet()
    }
}
