package com.example.kmpbackbone.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kmpbackbone.viewmodel.ProgressUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.progress_loading
import kmpbackbone.composeapp.generated.resources.progress_title
import org.jetbrains.compose.resources.stringResource
import kotlinx.datetime.LocalDate

@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onDismissDetail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.progress_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text(
                text = stringResource(Res.string.progress_loading),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
