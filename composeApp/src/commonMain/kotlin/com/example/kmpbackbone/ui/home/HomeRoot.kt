package com.example.kmpbackbone.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kmpbackbone.viewmodel.HomeUiEvent
import com.example.kmpbackbone.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    onOpenResult: (Long) -> Unit = {},
    onEditPlan: () -> Unit = {},
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.OpenNightResult -> onOpenResult(event.nightId)
                HomeUiEvent.EditPlan -> onEditPlan()
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onPlay = viewModel::onPlay,
        onStop = viewModel::onStop,
        onClaimResult = viewModel::onClaimResult,
        onEditPlan = viewModel::onEditPlan,
    )
}
