package com.example.kmpbackbone.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A3A5F),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF2B6E6B),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFE09F3E),
    onTertiary = Color(0xFF1B1B1B),
    background = Color(0xFFF8F5F0),
    onBackground = Color(0xFF141414),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFE9E2D9),
    onSurfaceVariant = Color(0xFF4A453E),
    outline = Color(0xFF9A9288),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FB3FF),
    onPrimary = Color(0xFF0C1B2A),
    secondary = Color(0xFF7CD6D1),
    onSecondary = Color(0xFF10312F),
    tertiary = Color(0xFFF2B96D),
    onTertiary = Color(0xFF2A1A00),
    background = Color(0xFF0B0F14),
    onBackground = Color(0xFFE5E3DC),
    surface = Color(0xFF11161C),
    onSurface = Color(0xFFE5E3DC),
    surfaceVariant = Color(0xFF1D2530),
    onSurfaceVariant = Color(0xFFBAC2CF),
    outline = Color(0xFF6B7685),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
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
            fontWeight = FontWeight.SemiBold,
            fontSize = 44.sp,
            lineHeight = 48.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 32.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        ),
    )
}

private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = appTypography(),
        shapes = AppShapes,
        content = content,
    )
}
