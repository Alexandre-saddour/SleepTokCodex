package com.example.kmpbackbone.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.kmpbackbone.ui.settings.SettingsRoot
import com.example.kmpbackbone.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot() {
    var showSettings by remember { mutableStateOf(false) }
    if (showSettings) {
        SettingsRoot(onBack = { showSettings = false })
    } else {
        val viewModel: ProfileViewModel = koinViewModel()
        val uiState by viewModel.state.collectAsState()
        ProfileScreen(
            uiState = uiState,
            onRefresh = viewModel::load,
            onOpenSettings = { showSettings = true },
        )
    }
}
