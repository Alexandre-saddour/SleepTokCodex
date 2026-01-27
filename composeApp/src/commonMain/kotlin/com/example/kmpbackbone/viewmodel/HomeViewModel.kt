package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Night
import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.usecase.GetHomeSummaryUseCase
import com.example.domain.usecase.StartNightUseCase
import com.example.domain.usecase.StopNightUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeMode {
    data object BeforeNight : HomeMode()
    data object NightMode : HomeMode()
    data object PostStopClaim : HomeMode()
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val isActionInProgress: Boolean = false,
    val user: User? = null,
    val plan: SleepPlan? = null,
    val activeNight: Night? = null,
    val mode: HomeMode = HomeMode.BeforeNight,
    val nextRewardWins: Int = 3,
    val error: DomainError? = null,
) : UiState

sealed class HomeUiEvent : UiEvent {
    data class OpenNightResult(val nightId: Long) : HomeUiEvent()
    data object EditPlan : HomeUiEvent()
}

class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val startNightUseCase: StartNightUseCase,
    private val stopNightUseCase: StopNightUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events = _events.asSharedFlow()

    init {
        loadHome()
    }

    fun onPlay() {
        executeNightAction { startNightUseCase.execute(Clock.System.now()) }
    }

    fun onStop() {
        executeNightAction { stopNightUseCase.execute(Clock.System.now()) }
    }

    private fun executeNightAction(action: suspend () -> AppResult<Night>) {
        viewModelScope.launch {
            _state.update { it.copy(isActionInProgress = true, error = null) }
            when (val result = action()) {
                is AppResult.Success -> updateNight(result.value)
                is AppResult.Error -> _state.update { it.copy(error = result.error) }
            }
            _state.update { it.copy(isActionInProgress = false) }
        }
    }

    fun onClaimResult() {
        viewModelScope.launch {
            val nightId = _state.value.activeNight?.id ?: return@launch
            _events.emit(HomeUiEvent.OpenNightResult(nightId))
        }
    }

    fun onEditPlan() {
        viewModelScope.launch {
            _events.emit(HomeUiEvent.EditPlan)
        }
    }

    fun refresh() {
        loadHome()
    }

    private fun loadHome() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val summaryResult = getHomeSummaryUseCase.execute()) {
                is AppResult.Success -> {
                    val summary = summaryResult.value
                    _state.update {
                        it.copy(
                            user = summary.user,
                            plan = summary.plan,
                            activeNight = summary.activeNight,
                            mode = modeForNight(summary.activeNight),
                        )
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(error = summaryResult.error) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun updateNight(night: Night?) {
        _state.update { current ->
            current.copy(
                activeNight = night,
                mode = modeForNight(night),
            )
        }
    }

    private fun modeForNight(night: Night?): HomeMode {
        return when {
            night == null -> HomeMode.BeforeNight
            night.endAt == null -> HomeMode.NightMode
            else -> HomeMode.PostStopClaim
        }
    }
}
