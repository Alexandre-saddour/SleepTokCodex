package com.example.kmpbackbone.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.kmpbackbone.viewmodel.OnboardingUiEvent
import com.example.kmpbackbone.viewmodel.OnboardingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingRoot(
    onCompleted: () -> Unit,
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is OnboardingUiEvent.Completed) {
                onCompleted()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
        }
    }
}
