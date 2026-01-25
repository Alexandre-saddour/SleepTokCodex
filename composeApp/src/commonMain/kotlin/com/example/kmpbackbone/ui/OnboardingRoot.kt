package com.example.kmpbackbone.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.example.kmpbackbone.ui.onboarding.OnboardingScreen
import com.example.kmpbackbone.viewmodel.OnboardingUiEvent
import com.example.kmpbackbone.viewmodel.OnboardingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingRoot(
    onCompleted: () -> Unit,
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    val currentOnCompleted by rememberUpdatedState(onCompleted)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is OnboardingUiEvent.Completed) {
                currentOnCompleted()
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onNext = viewModel::onNext,
        onBack = viewModel::onBack,
        onComplete = viewModel::onComplete,
        onGoalSelected = viewModel::onGoalSelected,
        onCoachStyleSelected = viewModel::onCoachStyleSelected,
        onBedtimeChanged = viewModel::onBedtimeChanged,
        onWakeTimeChanged = viewModel::onWakeTimeChanged,
        onActiveDayToggled = viewModel::onActiveDayToggled,
    )
}
