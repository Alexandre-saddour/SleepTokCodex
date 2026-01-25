
1- In NightResultViewModel the load function currently fetches data for night, summary, talentTree, and shield sequentially.  Since getHomeSummaryUseCase, getTalentTreeUseCase, and getStreakShieldUseCase are independent, they can be executed in parallel using async and awaitAll to improve performance and reduce loading time, making the UI more responsive. (Note: You will need to add import kotlinx.coroutines.async

2-  There's a UX issue in the current implementation of the streak shield flow. When the user taps "Use streak shield", the applyResult function is called, which updates the state and then immediately triggers navigation. This prevents the user from seeing the updated UI that confirms the shield was used (e.g., the "Shield used: streak preserved" message).

The intended flow should likely be:

User taps "Use streak shield".
The screen updates to show the result of using the shield.
The user then taps "Continue" to navigate away.

To fix this, I suggest refactoring the logic to separate the state update from the navigation. onUseShield should only apply the shield and update the UI state. onContinue should be responsible for navigation, applying the result if it hasn't been already.

    fun onContinue() {
        viewModelScope.launch {
            val snapshot = _state.value
            val result = snapshot.result ?: return@launch
            if (snapshot.isApplied) {
                emitNavigateBack()
                return@launch
            }

            _state.update { it.copy(isApplying = true, error = null) }
            val applyResult = applyNightResultUseCase.execute(
                night = snapshot.night!!,
                result = result,
                consumeShield = false,
            )
            when (applyResult) {
                is AppResult.Success -> {
                    emitNavigateBack()
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isApplying = false, error = applyResult.error) }
                }
            }
        }
    }

    fun onUseShield() {
        viewModelScope.launch {
            val snapshot = _state.value
            val result = snapshot.result ?: return@launch
            if (snapshot.isApplied) {
                return@launch
            }

            val preserved = result.copy(streakAfter = result.streakBefore)
            _state.update { it.copy(isApplying = true, error = null) }
            val applyResult = applyNightResultUseCase.execute(
                night = snapshot.night!!,
                result = preserved,
                consumeShield = true,
            )
            when (applyResult) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isApplying = false,
                            isApplied = true,
                            shieldUsed = true,
                            result = preserved,
                        )
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(isApplying = false, error = applyResult.error) }
                }
            }
        }
    }

3- In ProgressViewModel The ViewModel properties showWeeklyRecap and showAdvancedCalendar are used like temporary variables within loadMonth(). They are assigned and then immediately used to update the UI state, but are not read from anywhere else in the class.

To simplify the ViewModel's state and improve clarity, these can be removed as properties and declared as local vals inside the loadMonth() function. This reduces the state surface of the class.

4-
The parseTimeZone function is duplicated in both ProgressViewModel and NightResultViewModel. This violates the DRY (Don't Repeat Yourself) principle.

To improve maintainability and ensure consistency, this utility function should be extracted to a common location, such as a util package, and made public (e.g., as a top-level function).

5-
The labels list is created inside the CalendarHeader composable, which means it will be re-allocated on every recomposition. To improve performance and follow best practices, this constant list should be defined as a top-level constant outside the composable function.

private val weekdayLabels = listOf(
Res.string.day_mon_short,
Res.string.day_tue_short,
Res.string.day_wed_short,
Res.string.day_thu_short,
Res.string.day_fri_short,
Res.string.day_sat_short,
Res.string.day_sun_short,
)

@Composable
private fun CalendarHeader() {
Row(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceBetween,
) {
weekdayLabels.forEach { label ->
Text(
text = stringResource(label),
style = MaterialTheme.typography.labelLarge,
modifier = Modifier.weight(1f),
textAlign = TextAlign.Center,
)
}
}
}

6- The statusColor and contentColor are determined using two separate when expressions that have the same structure. This can be refactored into a single when expression that returns a Pair of colors. This change improves code readability and maintainability by reducing duplication.

7- 
in composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/progress/ProgressScreen.kt The colors for the legend items in LegendRow are hardcoded. This duplicates the color logic from CalendarCell and can lead to inconsistencies if the colors are updated in one place but not the other. To improve maintainability, the legend should be data-driven, deriving its colors from the NightStatus enum. This ensures the legend always reflects the colors used in the calendar grid.

A further improvement would be to extract the color selection logic into a shared helper function used by both CalendarCell and LegendRow to create a single source of truth.

8- in composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/progress/ProgressScreen.kt
To improve component reusability and make WeeklyRecapCard more stateless, it's a good practice to pass only the specific data a composable needs, rather than the entire state object. This decouples WeeklyRecapCard from the broader ProgressUiState.
suggestion: uiState.weeklyRecap?.let { WeeklyRecapCard(it) }

9-
There is some repetition in creating Text composables for each statistic, as they all share the same style (MaterialTheme.typography.bodyLarge). To improve maintainability and reduce boilerplate code, you could extract this into a small, private composable. This way, if you need to change the styling for the stats later, you'll only need to do it in one place.

For example:

@Composable
private fun RecapStat(text: String) {
Text(text = text, style = MaterialTheme.typography.bodyLarge)
}

You could then use RecapStat(...) for each of the four statistics in the WeeklyRecapCard

10-
Using ?: 0 for actualDurationMinutes, score, and xpEarned can be misleading when these values are null (e.g., for an in-progress night). This will display values as 0, which is inaccurate for an ongoing or unscored night. For example, "Score: 0" suggests poor performance, not an unevaluated state.

It's better to handle null values explicitly:

For actualDurationMinutes, show a placeholder like "N/A".
For score and xpEarned, conditionally render the Text composables only if the values are not null.
Text(
text = stringResource(
Res.string.progress_detail_plan_vs_actual,
formatMinutes(night.planDurationMinutes),
night.actualDurationMinutes?.let { formatMinutes(it) } ?: "N/A",
),
style = MaterialTheme.typography.bodyLarge,
)
night.score?.let { score ->
Text(
text = stringResource(
Res.string.progress_detail_score,
score,
),
style = MaterialTheme.typography.bodyLarge,
)
}
night.xpEarned?.let { xp ->
Text(
text = stringResource(
Res.string.progress_detail_xp,
xp,
),
style = MaterialTheme.typography.bodyLarge,
)
}


11-Mapping NightStatus.IN_PROGRESS to "PARTIAL" could be misleading. An in-progress night is still ongoing and hasn't been evaluated yet, whereas "PARTIAL" implies a final, albeit incomplete, result. This could confuse the user about the state of their night.

Consider adding a dedicated status string for "In progress" (e.g., in strings.xml) or preventing the detail overlay from being shown for nights that are still in progress.


12- in composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/talents/TalentsScreen.kt
The current implementation filters the list of all talents for each talent branch inside a forEach loop. While this works for a small number of talents, it's inefficient as it iterates over the full list of talents for every branch (O(branches * talents)).

A more efficient and readable approach is to group the talents by branch once using groupBy before iterating through the branches.

            val talentsByBranch = uiState.talents.groupBy { it.talent.branch }
            TalentBranch.values().forEach { branch ->
                TalentBranchSection(
                    branch = branch,
                    talents = talentsByBranch[branch].orEmpty(),
                    onUnlock = onUnlock,
                    isUnlocking = uiState.isUnlocking,
                )
            }

13- in composeApp/src/commonMain/kotlin/com/example/kmpbackbone/ui/talents/TalentsScreen.kt in talentNameRes func
The else branch in talentNameRes (and talentDescRes) falls back to Res.string.talents_title. This can hide issues with missing or mistyped talent keys by displaying the screen title instead of the talent's name or description, which is confusing for users and makes debugging harder.

To make these issues more apparent during development, consider throwing an IllegalArgumentException for unknown keys. This fail-fast approach ensures that any new talents added to the system must have their corresponding string resources correctly mapped. A similar change should be applied to talentDescRes.

private fun talentNameRes(key: String): StringResource {
return when (key) {
"talent_d1_name" -> Res.string.talent_d1_name
"talent_d2_name" -> Res.string.talent_d2_name
"talent_d3_name" -> Res.string.talent_d3_name
"talent_s1_name" -> Res.string.talent_s1_name
"talent_s2_name" -> Res.string.talent_s2_name
"talent_s3_name" -> Res.string.talent_s3_name
"talent_t1_name" -> Res.string.talent_t1_name
"talent_t2_name" -> Res.string.talent_t2_name
"talent_t3_name" -> Res.string.talent_t3_name
"talent_i1_name" -> Res.string.talent_i1_name
"talent_i2_name" -> Res.string.talent_i2_name
"talent_i3_name" -> Res.string.talent_i3_name
else -> throw IllegalArgumentException("Unknown talent name key: $key")
}
}


