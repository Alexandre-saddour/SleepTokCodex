package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CoachStyle
import com.example.domain.model.PremiumStatus
import com.example.domain.model.SleepPlan
import com.example.domain.model.User
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.usecase.CompleteOnboardingUseCase
import com.example.domain.usecase.GetOnboardingStateUseCase
import kotlin.math.abs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

enum class OnboardingGoal {
    BETTER_SLEEP,
    ROUTINE,
    PRODUCTIVITY,
    EARLY_WAKE,
}

enum class OnboardingStep {
    WELCOME,
    GOAL,
    COACH_STYLE,
    PLAN,
    GAMIFICATION,
    READY,
}

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val stepIndex: Int = 0,
    val selectedGoal: OnboardingGoal? = null,
    val coachStyle: CoachStyle? = null,
    val bedtime: LocalTime = LocalTime(hour = 23, minute = 30),
    val wakeTime: LocalTime = LocalTime(hour = 7, minute = 30),
    val activeDays: Set<DayOfWeek> = DayOfWeek.values().toSet(),
    val timeZoneId: String = TimeZone.currentSystemDefault().id,
    val error: DomainError? = null,
) : UiState

sealed class OnboardingUiEvent : UiEvent {
    data object Completed : OnboardingUiEvent()
}

class OnboardingViewModel(
    private val getOnboardingStateUseCase: GetOnboardingStateUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {
    private val steps = OnboardingStep.values()
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingUiEvent>()
    val events = _events.asSharedFlow()

    init {
        loadOnboardingState()
    }

    fun onGoalSelected(goal: OnboardingGoal) {
        _state.update { it.copy(selectedGoal = goal) }
    }

    fun onCoachStyleSelected(style: CoachStyle) {
        _state.update { it.copy(coachStyle = style) }
    }

    fun onBedtimeChanged(time: LocalTime) {
        _state.update { it.copy(bedtime = time) }
    }

    fun onWakeTimeChanged(time: LocalTime) {
        _state.update { it.copy(wakeTime = time) }
    }

    fun onActiveDayToggled(day: DayOfWeek) {
        _state.update { current ->
            val updated = current.activeDays.toMutableSet()
            if (updated.contains(day)) {
                updated.remove(day)
            } else {
                updated.add(day)
            }
            current.copy(activeDays = updated)
        }
    }

    fun onNext() {
        _state.update { current ->
            val nextIndex = (current.stepIndex + 1).coerceAtMost(steps.lastIndex)
            current.copy(stepIndex = nextIndex)
        }
    }

    fun onBack() {
        _state.update { current ->
            val prevIndex = (current.stepIndex - 1).coerceAtLeast(0)
            current.copy(stepIndex = prevIndex)
        }
    }

    fun onComplete() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val snapshot = state.value
            val user = buildUser(snapshot)
            val plan = buildPlan(snapshot)
            when (val result = completeOnboardingUseCase.execute(user, plan)) {
                is AppResult.Success -> _events.emit(OnboardingUiEvent.Completed)
                is AppResult.Error -> _state.update { it.copy(isLoading = false, error = result.error) }
            }
        }
    }

    private fun loadOnboardingState() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getOnboardingStateUseCase.execute()) {
                is AppResult.Success -> {
                    val onboarding = result.value
                    if (onboarding.isComplete) {
                        _events.emit(OnboardingUiEvent.Completed)
                    } else {
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                coachStyle = onboarding.user?.coachStyle ?: current.coachStyle,
                                timeZoneId = onboarding.user?.timezone ?: current.timeZoneId,
                                bedtime = onboarding.plan?.planStartLocalTime ?: current.bedtime,
                                wakeTime = onboarding.plan?.planEndLocalTime ?: current.wakeTime,
                                activeDays = onboarding.plan?.activeDaysMask?.let { activeDaysFromMask(it) }
                                    ?: current.activeDays,
                            )
                        }
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    private fun buildUser(state: OnboardingUiState): User {
        val now = Clock.System.now()
        val baseline = planDurationMinutes(state.bedtime, state.wakeTime)
        return User(
            id = 0L,
            createdAt = now,
            timezone = state.timeZoneId,
            coachStyle = state.coachStyle ?: CoachStyle.CHILL,
            premiumStatus = PremiumStatus.NONE,
            premiumUntil = null,
            level = 1,
            xpTotal = 0,
            talentPointsAvailable = 0,
            streakCurrent = 0,
            streakBest = 0,
            lastNightId = null,
            baselineSleepDurationMinutes = baseline,
            settingsJson = null,
        )
    }

    private fun buildPlan(state: OnboardingUiState): SleepPlan {
        val now = Clock.System.now()
        return SleepPlan(
            id = 0L,
            userId = 0L,
            planStartLocalTime = state.bedtime,
            planEndLocalTime = state.wakeTime,
            activeDaysMask = activeDaysMask(state.activeDays),
            toleranceStartMinutes = 15,
            toleranceEndMinutes = 20,
            createdAt = now,
            isActive = true,
        )
    }

    private fun activeDaysMask(days: Set<DayOfWeek>): Int {
        return days.fold(0) { mask, day ->
            mask or (1 shl day.ordinal)
        }
    }

    private fun activeDaysFromMask(mask: Int): Set<DayOfWeek> {
        return DayOfWeek.values()
            .filter { day -> mask and (1 shl day.ordinal) != 0 }
            .toSet()
    }

    private fun planDurationMinutes(start: LocalTime, end: LocalTime): Int {
        val startMinutes = start.hour * 60 + start.minute
        val endMinutes = end.hour * 60 + end.minute
        val raw = if (endMinutes >= startMinutes) {
            endMinutes - startMinutes
        } else {
            (24 * 60 - startMinutes) + endMinutes
        }
        return abs(raw)
    }
}
