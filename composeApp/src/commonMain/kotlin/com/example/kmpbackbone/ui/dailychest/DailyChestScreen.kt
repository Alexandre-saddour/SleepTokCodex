package com.example.kmpbackbone.ui.dailychest

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.Reward
import com.example.domain.result.DomainError
import com.example.kmpbackbone.viewmodel.DailyChestUiState
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.daily_chest_claimed_title
import kmpbackbone.composeapp.generated.resources.daily_chest_cta_ok
import kmpbackbone.composeapp.generated.resources.daily_chest_error_generic
import kmpbackbone.composeapp.generated.resources.daily_chest_error_not_found
import kmpbackbone.composeapp.generated.resources.daily_chest_error_storage
import kmpbackbone.composeapp.generated.resources.daily_chest_error_validation
import kmpbackbone.composeapp.generated.resources.daily_chest_loading
import kmpbackbone.composeapp.generated.resources.daily_chest_reward_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun DailyChestModal(
    uiState: DailyChestUiState,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            when {
                uiState.isClaiming -> LoadingContent()
                uiState.claimedReward != null -> RewardContent(
                    reward = uiState.claimedReward,
                    onDismiss = onDismiss,
                )
                uiState.error != null -> ErrorContent(
                    error = uiState.error,
                    onDismiss = onDismiss,
                )
                else -> {
                    // Should not happen
                    onDismiss()
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(Res.string.daily_chest_loading),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun RewardContent(
    reward: Reward,
    onDismiss: () -> Unit,
) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing),
        )
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(800, easing = LinearEasing),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.daily_chest_claimed_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    rotationZ = rotation.value,
                ),
            contentAlignment = Alignment.Center,
        ) {
            val glow = Brush.radialGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
                    Color.Transparent,
                ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(glow, CircleShape),
            )
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = CircleShape,
                modifier = Modifier.size(100.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = "\u2728",
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
            }
        }

        Text(
            text = stringResource(Res.string.daily_chest_reward_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Text(
            text = reward.nameKey,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(text = stringResource(Res.string.daily_chest_cta_ok))
        }
    }
}

@Composable
private fun ErrorContent(
    error: DomainError,
    onDismiss: () -> Unit,
) {
    val message = when (error) {
        DomainError.NotFound -> Res.string.daily_chest_error_not_found
        DomainError.Validation -> Res.string.daily_chest_error_validation
        DomainError.Storage -> Res.string.daily_chest_error_storage
        else -> Res.string.daily_chest_error_generic
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(text = stringResource(Res.string.daily_chest_cta_ok))
        }
    }
}
