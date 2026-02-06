package com.example.kmpbackbone.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kmpbackbone.ui.theme.NeonColors

/**
 * Animated gradient background with subtle moving neon colors.
 */
@Composable
fun NeonGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient-animation")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "gradient-offset",
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            NeonColors.NeonDarkBackground,
            NeonColors.NeonDarkSurface.copy(alpha = 0.95f + animatedOffset * 0.05f),
            NeonColors.NeonDarkBackground,
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
    ) {
        content()
    }
}

/**
 * Glassmorphism-style card with neon glow border.
 */
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeonColors.NeonElectricBlue,
    glowIntensity: Float = 0.4f,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        // Glow layer (blur behind)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp)
                .blur(12.dp)
                .background(
                    glowColor.copy(alpha = glowIntensity),
                    RoundedCornerShape(24.dp),
                ),
        )
        // Main card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = NeonColors.NeonDarkSurface.copy(alpha = 0.85f),
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.6f),
                            glowColor.copy(alpha = 0.2f),
                            glowColor.copy(alpha = 0.6f),
                        ),
                    ),
                    shape = RoundedCornerShape(24.dp),
                ),
        ) {
            content()
        }
    }
}

/**
 * Neon-styled button with gradient and glow effect.
 */
@Composable
fun NeonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color = NeonColors.NeonElectricBlue,
    content: @Composable () -> Unit,
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            glowColor,
            glowColor.copy(alpha = 0.8f),
        ),
    )

    Box(modifier = modifier) {
        // Glow layer
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(4.dp)
                    .blur(16.dp)
                    .background(
                        glowColor.copy(alpha = 0.5f),
                        RoundedCornerShape(18.dp),
                    ),
            )
        }
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = glowColor,
                contentColor = NeonColors.NeonDarkBackground,
                disabledContainerColor = NeonColors.NeonDarkSurfaceVariant,
                disabledContentColor = NeonColors.TextMuted,
            ),
        ) {
            content()
        }
    }
}

/**
 * Progress bar with neon gradient and glow.
 */
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = NeonColors.NeonGreen,
    trackColor: Color = NeonColors.NeonDarkSurfaceVariant,
    height: Dp = 10.dp,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2)),
    ) {
        // Track
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(trackColor),
        )
        // Progress with glow
        Box(
            modifier = Modifier
                .fillMaxWidth(clampedProgress)
                .height(height)
                .drawBehind {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                progressColor.copy(alpha = 0.3f),
                                progressColor,
                            ),
                        ),
                    )
                }
                .blur(4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(clampedProgress)
                .height(height)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            progressColor.copy(alpha = 0.8f),
                            progressColor,
                        ),
                    ),
                    RoundedCornerShape(height / 2),
                ),
        )
    }
}

/**
 * Badge component for streak/level display with neon styling.
 */
@Composable
fun NeonBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonColors.NeonGreen,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color.copy(alpha = 0.15f),
        contentColor = color,
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.5f),
                    shape = CircleShape,
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = color,
            )
        }
    }
}

/**
 * Calendar cell with neon status glow.
 */
@Composable
fun NeonStatusCell(
    dayNumber: String,
    score: String? = null,
    statusColor: Color?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val backgroundColor = when {
        statusColor != null -> statusColor.copy(alpha = 0.15f)
        else -> NeonColors.NeonDarkSurfaceVariant
    }
    val borderColor = statusColor?.copy(alpha = 0.5f) ?: NeonColors.Outline

    Surface(
        modifier = modifier
            .height(54.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.small,
            ),
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        contentColor = statusColor ?: NeonColors.TextSecondary,
        onClick = onClick ?: {},
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor ?: NeonColors.TextPrimary,
            )
            if (score != null) {
                Text(
                    text = score,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor ?: NeonColors.TextSecondary,
                )
            }
        }
    }
}

/**
 * Score ring component with neon glow based on score.
 */
@Composable
fun NeonScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
) {
    val scoreColor = when {
        score >= 80 -> NeonColors.NeonGreen
        score >= 50 -> NeonColors.NeonYellow
        else -> NeonColors.StatusFail
    }
    val progress = (score.coerceIn(0, 100)) / 100f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(size)
                .blur(16.dp)
                .background(
                    scoreColor.copy(alpha = 0.3f),
                    CircleShape,
                ),
        )
        // Background circle
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = NeonColors.NeonDarkSurface,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 8.dp,
                        brush = Brush.sweepGradient(
                            0f to scoreColor.copy(alpha = 0.2f),
                            progress to scoreColor,
                            progress to NeonColors.NeonDarkSurfaceVariant,
                            1f to NeonColors.NeonDarkSurfaceVariant,
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = scoreColor,
                )
            }
        }
    }
}

/**
 * Talent card with branch-specific neon color.
 */
@Composable
fun NeonTalentCard(
    title: String,
    description: String,
    branchColor: Color,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    actionContent: @Composable () -> Unit = {},
) {
    val cardAlpha = if (isUnlocked) 0.9f else 0.6f
    val glowIntensity = if (isUnlocked) 0.4f else 0.15f

    NeonCard(
        modifier = modifier,
        glowColor = branchColor,
        glowIntensity = glowIntensity,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isUnlocked) branchColor else NeonColors.TextSecondary,
                )
                // Status indicator dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isUnlocked) branchColor else NeonColors.TextMuted,
                            shape = CircleShape,
                        ),
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = NeonColors.TextSecondary.copy(alpha = cardAlpha),
            )
            actionContent()
        }
    }
}

/**
 * Section header with neon accent line.
 */
@Composable
fun NeonSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonColors.NeonElectricBlue,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = NeonColors.TextPrimary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0f),
                        ),
                    ),
                ),
        )
    }
}
