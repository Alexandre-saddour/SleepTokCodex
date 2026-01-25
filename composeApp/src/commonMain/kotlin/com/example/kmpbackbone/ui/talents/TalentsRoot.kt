package com.example.kmpbackbone.ui.talents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kmpbackbone.viewmodel.TalentsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TalentsRoot() {
    val viewModel: TalentsViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    TalentsScreen(
        uiState = uiState,
        onUnlock = viewModel::unlockTalent,
        onRefresh = viewModel::load,
    )
}
