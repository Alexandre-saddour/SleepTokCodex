package com.example.kmpbackbone.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kmpbackbone.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    onOpenSettings: () -> Unit,
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()
    ProfileScreen(
        uiState = uiState,
        onRefresh = viewModel::load,
        onOpenSettings = onOpenSettings,
    )
}
