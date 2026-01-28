package com.example.kmpbackbone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Night
import com.example.domain.model.NightStatus
import com.example.domain.model.Talent
import com.example.domain.model.TalentEffect
import com.example.domain.model.WeeklyRecap
import com.example.domain.result.AppResult
import com.example.domain.result.DomainError
import com.example.domain.usecase.GetCalendarMonthUseCase
import com.example.domain.usecase.GetHomeSummaryUseCase
import com.example.domain.usecase.GetNightDetailUseCase
import com.example.domain.usecase.GetTalentTreeUseCase
import com.example.domain.usecase.GetWeeklyRecapUseCase
import com.example.domain.util.toLocalDateTime
import com.example.kmpbackbone.util.parseTimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

data class CalendarDayUi(
    val date: LocalDate,
    val nightId: Long?,
    val status: NightStatus?,
    val score: Int?,
)

data class ProgressUiState(
    val isLoading: Boolean = true,
    val monthStart: LocalDate? = null,
    val days: List<CalendarDayUi> = emptyList(),
    val weeklyRecap: WeeklyRecap? = null,
    val showWeeklyRecap: Boolean = false,
    val showAdvancedCalendar: Boolean = false,
    val selectedNight: Night? = null,
    val error: DomainError? = null,
) : UiState

class ProgressViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val getCalendarMonthUseCase: GetCalendarMonthUseCase,
    private val getWeeklyRecapUseCase: GetWeeklyRecapUseCase,
    private val getNightDetailUseCase: GetNightDetailUseCase,
    private val getTalentTreeUseCase: GetTalentTreeUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state.asStateFlow()

    private var currentMonthStart: LocalDate? = null
    private var timeZone = TimeZone.currentSystemDefault()

    init {
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        loadMonth(today.year, today.monthNumber)
    }

    fun loadMonth(year: Int, monthNumber: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val summaryResult = getHomeSummaryUseCase.execute()
            if (summaryResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = summaryResult.error) }
                return@launch
            }
            val summary = (summaryResult as AppResult.Success).value
            timeZone = parseTimeZone(summary.user.timezone)

            val talentTreeResult = getTalentTreeUseCase.execute()
            if (talentTreeResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = talentTreeResult.error) }
                return@launch
            }
            val talentTree = (talentTreeResult as AppResult.Success).value
            val showWeeklyRecap = hasEffect(
                talentTree.talents,
                talentTree.unlockedTalentIds,
                TalentEffect.EnableWeeklyRecap,
            )
            val showAdvancedCalendar = hasEffect(
                talentTree.talents,
                talentTree.unlockedTalentIds,
                TalentEffect.EnableAdvancedCalendar,
            )

            val monthStart = LocalDate(year, monthNumber, 1)
            val monthEnd = monthStart.plus(1, DateTimeUnit.MONTH).plus(-1, DateTimeUnit.DAY)
            currentMonthStart = monthStart

            val nightsResult = getCalendarMonthUseCase.execute(monthStart, monthEnd, timeZone)
            if (nightsResult is AppResult.Error) {
                _state.update { it.copy(isLoading = false, error = nightsResult.error) }
                return@launch
            }
            val nights = (nightsResult as AppResult.Success).value
            val days = buildCalendarDays(monthStart, monthEnd, nights)

            val weeklyRecap = if (showWeeklyRecap) {
                loadWeeklyRecap()
            } else {
                null
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    monthStart = monthStart,
                    days = days,
                    weeklyRecap = weeklyRecap,
                    showWeeklyRecap = showWeeklyRecap,
                    showAdvancedCalendar = showAdvancedCalendar,
                )
            }
        }
    }

    fun goToPreviousMonth() {
        val current = currentMonthStart ?: return
        val previous = current.plus(-1, DateTimeUnit.MONTH)
        loadMonth(previous.year, previous.monthNumber)
    }

    fun goToNextMonth() {
        val current = currentMonthStart ?: return
        val next = current.plus(1, DateTimeUnit.MONTH)
        loadMonth(next.year, next.monthNumber)
    }

    fun onDaySelected(date: LocalDate) {
        val day = _state.value.days.firstOrNull { it.date == date } ?: return
        val nightId = day.nightId ?: return
        viewModelScope.launch {
            val nightResult = getNightDetailUseCase.execute(nightId)
            when (nightResult) {
                is AppResult.Success -> _state.update { it.copy(selectedNight = nightResult.value) }
                is AppResult.Error -> _state.update { it.copy(error = nightResult.error) }
            }
        }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedNight = null) }
    }

    private suspend fun loadWeeklyRecap(): WeeklyRecap? {
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val weekStart = weekStartFor(today)
        val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)
        val recapResult = getWeeklyRecapUseCase.execute(weekStart, weekEnd, timeZone)
        return if (recapResult is AppResult.Success) recapResult.value else null
    }

    private fun buildCalendarDays(
        start: LocalDate,
        end: LocalDate,
        nights: List<Night>,
    ): List<CalendarDayUi> {
        val nightMap = nights.associateBy { it.startAt.toLocalDateTime(timeZone).date }
        val days = mutableListOf<CalendarDayUi>()
        var current = start
        while (current <= end) {
            val night = nightMap[current]
            days.add(
                CalendarDayUi(
                    date = current,
                    nightId = night?.id,
                    status = night?.status,
                    score = night?.score,
                )
            )
            current = current.plus(1, DateTimeUnit.DAY)
        }
        return days
    }

    private fun weekStartFor(date: LocalDate): LocalDate {
        val offset = date.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
        return date.plus(-offset, DateTimeUnit.DAY)
    }

    private fun hasEffect(
        talents: List<Talent>,
        unlockedIds: Set<String>,
        effect: TalentEffect,
    ): Boolean {
        return talents
            .filter { unlockedIds.contains(it.id) }
            .any { it.effect == effect }
    }
}
