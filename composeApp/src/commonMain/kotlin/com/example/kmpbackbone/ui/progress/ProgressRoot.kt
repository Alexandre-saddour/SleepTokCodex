package com.example.kmpbackbone.ui.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kmpbackbone.viewmodel.ProgressViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgressRoot() {
    val viewModel: ProgressViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    ProgressScreen(
        uiState = uiState,
        onPreviousMonth = viewModel::goToPreviousMonth,
        onNextMonth = viewModel::goToNextMonth,
        onDaySelected = viewModel::onDaySelected,
        onDismissDetail = viewModel::clearSelection,
    )
}
