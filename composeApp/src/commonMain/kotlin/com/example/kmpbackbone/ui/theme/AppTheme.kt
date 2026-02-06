package com.example.kmpbackbone.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpbackbone.composeapp.generated.resources.Res
import kmpbackbone.composeapp.generated.resources.space_grotesk_regular
import kmpbackbone.composeapp.generated.resources.space_grotesk_semibold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

/**
 * Neon Dark color scheme using the NeonColors palette.
 */
private val NeonDarkColors = darkColorScheme(
    primary = NeonColors.NeonElectricBlue,
    onPrimary = NeonColors.NeonDarkBackground,
    primaryContainer = NeonColors.NeonDarkSurfaceVariant,
    onPrimaryContainer = NeonColors.NeonElectricBlue,

    secondary = NeonColors.NeonCyan,
    onSecondary = NeonColors.NeonDarkBackground,
    secondaryContainer = NeonColors.NeonDarkSurfaceVariant,
    onSecondaryContainer = NeonColors.NeonCyan,

    tertiary = NeonColors.NeonYellow,
    onTertiary = NeonColors.NeonDarkBackground,
    tertiaryContainer = NeonColors.NeonDarkSurfaceVariant,
    onTertiaryContainer = NeonColors.NeonYellow,

    background = NeonColors.NeonDarkBackground,
    onBackground = NeonColors.TextPrimary,

    surface = NeonColors.NeonDarkSurface,
    onSurface = NeonColors.TextPrimary,

    surfaceVariant = NeonColors.NeonDarkSurfaceVariant,
    onSurfaceVariant = NeonColors.TextSecondary,

    outline = NeonColors.Outline,
    outlineVariant = NeonColors.TextMuted,

    error = NeonColors.Error,
    onError = NeonColors.OnError,
    errorContainer = NeonColors.StatusFail.copy(alpha = 0.2f),
    onErrorContainer = NeonColors.StatusFail,

    inverseSurface = NeonColors.TextPrimary,
    inverseOnSurface = NeonColors.NeonDarkBackground,
    inversePrimary = NeonColors.NeonElectricBlue,
)

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun appFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.space_grotesk_regular, FontWeight.Normal),
        Font(Res.font.space_grotesk_semibold, FontWeight.SemiBold),
    )
}

@Composable
private fun appTypography(): Typography {
    val fontFamily = appFontFamily()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 52.sp,
            lineHeight = 56.sp,
            letterSpacing = (-0.5).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 44.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 34.sp,
            lineHeight = 38.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            lineHeight = 36.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 32.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 28.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            lineHeight = 14.sp,
        ),
    )
}

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NeonDarkColors,
        typography = appTypography(),
        shapes = AppShapes,
        content = content,
    )
}
