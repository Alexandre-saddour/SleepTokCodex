package com.example.kmpbackbone.ui.dailychest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kmpbackbone.viewmodel.DailyChestUiEvent
import com.example.kmpbackbone.viewmodel.DailyChestViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DailyChestRoot(
    onDismiss: () -> Unit,
) {
    val viewModel: DailyChestViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.claimChest()
        viewModel.events.collectLatest { event ->
            when (event) {
                DailyChestUiEvent.Claimed -> onDismiss()
            }
        }
    }

    DailyChestModal(
        uiState = uiState,
        onDismiss = viewModel::onDismiss,
    )
}
