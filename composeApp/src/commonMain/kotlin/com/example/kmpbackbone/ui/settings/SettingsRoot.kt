package com.example.kmpbackbone.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kmpbackbone.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onBack = onBack,
        onRefresh = viewModel::load,
        onBedtimeChanged = viewModel::onBedtimeChanged,
        onWakeTimeChanged = viewModel::onWakeTimeChanged,
        onActiveDayToggled = viewModel::onActiveDayToggled,
        onCoachStyleSelected = viewModel::onCoachStyleSelected,
        onSave = viewModel::onSave,
    )
}
