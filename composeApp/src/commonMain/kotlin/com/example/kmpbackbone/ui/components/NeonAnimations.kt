package com.example.kmpbackbone.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.kmpbackbone.ui.theme.NeonColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Confetti particle data class.
 */
private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
)

/**
 * Confetti animation overlay for success celebrations.
 */
@Composable
fun NeonConfetti(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
) {
    if (!isActive) return

    val colors = listOf(
        NeonColors.NeonGreen,
        NeonColors.NeonElectricBlue,
        NeonColors.NeonPink,
        NeonColors.NeonYellow,
        NeonColors.NeonPurple,
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                velocityX = Random.nextFloat() * 0.02f - 0.01f,
                velocityY = Random.nextFloat() * 0.02f + 0.01f,
                color = colors.random(),
                size = Random.nextFloat() * 8f + 4f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 10f - 5f,
            )
        }
    }

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing),
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val progress = animationProgress.value
        particles.forEach { particle ->
            val currentX = (particle.x + particle.velocityX * progress * 50) * size.width
            val currentY = (particle.y + particle.velocityY * progress * 50) * size.height
            val alpha = 1f - progress

            if (currentY <= size.height && alpha > 0f) {
                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.size,
                    center = Offset(currentX, currentY),
                )
            }
        }
    }
}

/**
 * Pulsing glow effect modifier.
 */
@Composable
fun Modifier.pulsingGlow(
    glowColor: Color = NeonColors.NeonElectricBlue,
    enabled: Boolean = true,
): Modifier {
    if (!enabled) return this

    val infiniteTransition = rememberInfiniteTransition(label = "pulsing-glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow-alpha",
    )

    return this
        .blur(8.dp)
        .graphicsLayer {
            this.alpha = glowAlpha
        }
}

/**
 * Breathing scale animation for orbs and circular elements.
 */
@Composable
fun BreathingAnimation(
    modifier: Modifier = Modifier,
    content: @Composable (scale: Float, alpha: Float) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing-animation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathing-scale",
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathing-alpha",
    )

    Box(modifier = modifier) {
        content(scale, alpha)
    }
}

/**
 * Neon breathing orb with customizable gradient colors.
 */
@Composable
fun NeonBreathingOrb(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonColors.NeonCyan,
    secondaryColor: Color = NeonColors.NeonGreen,
    size: androidx.compose.ui.unit.Dp = 180.dp,
    coreSize: androidx.compose.ui.unit.Dp = 84.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon-orb")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-scale",
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-alpha",
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "color-shift",
    )

    val glowColor = lerp(primaryColor, secondaryColor, colorShift)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        // Outer glow
        Canvas(
            modifier = Modifier
                .blur(24.dp)
                .graphicsLayer { this.alpha = alpha * 0.5f }
        ) {
            val radius = size.toPx() / 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = alpha),
                        Color.Transparent,
                    ),
                ),
                radius = radius,
            )
        }
        // Inner glow
        Canvas(modifier = Modifier) {
            val radius = size.toPx() / 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = alpha),
                        Color.Transparent,
                    ),
                ),
                radius = radius,
            )
        }
        // Core
        Canvas(modifier = Modifier) {
            val radius = coreSize.toPx() / 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor,
                        glowColor.copy(alpha = 0.8f),
                    ),
                ),
                radius = radius,
            )
        }
    }
}

/**
 * Animated counter that counts up from 0 to target value.
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000,
    content: @Composable (currentValue: Int) -> Unit,
) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        animatedValue.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        )
    }

    Box(modifier = modifier) {
        content(animatedValue.value.toInt())
    }
}

/**
 * Reward reveal animation with burst effect.
 */
@Composable
fun RewardBurstAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    burstColor: Color = NeonColors.NeonYellow,
) {
    if (!isActive) return

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(800, easing = FastOutSlowInEasing),
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val progress = animationProgress.value
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension * 0.6f

        // Burst rays
        val rayCount = 12
        repeat(rayCount) { i ->
            val angle = (i * 360f / rayCount) * (PI / 180f)
            val rayLength = maxRadius * progress
            val rayAlpha = (1f - progress).coerceAtLeast(0f)

            val endX = center.x + cos(angle).toFloat() * rayLength
            val endY = center.y + sin(angle).toFloat() * rayLength

            drawLine(
                color = burstColor.copy(alpha = rayAlpha),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 4f * (1f - progress + 0.2f),
            )
        }

        // Center glow
        val glowRadius = maxRadius * 0.3f * (1f - progress)
        drawCircle(
            color = burstColor.copy(alpha = (1f - progress) * 0.5f),
            radius = glowRadius,
            center = center,
        )
    }
}

/**
 * Simple color interpolation.
 */
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction,
    )
}
