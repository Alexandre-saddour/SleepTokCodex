package com.example.kmpbackbone.ui.talents

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
import com.example.kmpbackbone.viewmodel.TalentsUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.talents_loading
import kmpbackbone.composeapp.generated.resources.talents_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TalentsScreen(
    uiState: TalentsUiState,
    onUnlock: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.talents_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text(
                text = stringResource(Res.string.talents_loading),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
