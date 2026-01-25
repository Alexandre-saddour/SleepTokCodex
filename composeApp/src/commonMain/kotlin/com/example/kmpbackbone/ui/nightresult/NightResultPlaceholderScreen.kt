package com.example.kmpbackbone.ui.nightresult

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.night_result_placeholder_body
import kmpbackbone.composeapp.generated.resources.night_result_placeholder_cta
import kmpbackbone.composeapp.generated.resources.night_result_placeholder_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NightResultPlaceholderScreen(
    nightId: Long,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.night_result_placeholder_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.night_result_placeholder_body, nightId),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
        )
        Button(onClick = onBack) {
            Text(text = stringResource(Res.string.night_result_placeholder_cta))
        }
    }
}
