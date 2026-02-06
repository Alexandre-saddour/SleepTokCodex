package com.example.kmpbackbone.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Neon Dark color palette for SleepTok app.
 * A Gen Z-friendly "Neon Dark" aesthetic with vibrant accent colors.
 */
object NeonColors {

    // ========================
    // BACKGROUNDS
    // ========================

    /** Near-black with blue tint - main background */
    val NeonDarkBackground = Color(0xFF0A0A0F)

    /** Elevated dark surface */
    val NeonDarkSurface = Color(0xFF12121A)

    /** Cards and elevated containers */
    val NeonDarkSurfaceVariant = Color(0xFF1A1A26)

    // ========================
    // NEON ACCENTS
    // ========================

    /** Primary CTAs, navigation, links */
    val NeonElectricBlue = Color(0xFF00D9FF)

    /** Secondary highlights */
    val NeonCyan = Color(0xFF00FFE0)

    /** Success states, streaks, achievements */
    val NeonGreen = Color(0xFF00FF88)

    /** Special highlights, talents, rewards */
    val NeonPink = Color(0xFFFF00AA)

    /** Tertiary accents */
    val NeonPurple = Color(0xFFBB00FF)

    /** Warnings */
    val NeonOrange = Color(0xFFFF6B00)

    /** Rewards, gold, XP */
    val NeonYellow = Color(0xFFFFE500)

    // ========================
    // STATUS COLORS
    // ========================

    /** Success status - bright green */
    val StatusSuccess = Color(0xFF00FF88)

    /** Partial success - amber */
    val StatusPartial = Color(0xFFFFB800)

    /** Fail status - hot pink */
    val StatusFail = Color(0xFFFF3366)

    // ========================
    // TEXT COLORS
    // ========================

    /** Primary text - white */
    val TextPrimary = Color(0xFFFFFFFF)

    /** Secondary text - light gray with hint of blue */
    val TextSecondary = Color(0xFFB8B8C8)

    /** Muted text - dim gray */
    val TextMuted = Color(0xFF6B6B80)

    // ========================
    // UTILITY COLORS
    // ========================

    /** Outline color for borders */
    val Outline = Color(0xFF3A3A4D)

    /** Error state */
    val Error = Color(0xFFFF3366)

    /** On error - text on error background */
    val OnError = Color(0xFFFFFFFF)

    // ========================
    // GLOW / EFFECT COLORS
    // ========================

    /** Glow color for electric blue elements (50% alpha) */
    val GlowElectricBlue = Color(0x8000D9FF)

    /** Glow color for green elements (50% alpha) */
    val GlowGreen = Color(0x8000FF88)

    /** Glow color for pink elements (50% alpha) */
    val GlowPink = Color(0x80FF00AA)

    /** Glow color for purple elements (50% alpha) */
    val GlowPurple = Color(0x80BB00FF)

    /** Glow color for yellow/gold elements (50% alpha) */
    val GlowYellow = Color(0x80FFE500)

    // ========================
    // BRANCH-SPECIFIC COLORS (for Talents)
    // ========================

    /** Discipline branch - Electric Blue */
    val BranchDiscipline = NeonElectricBlue

    /** Streak branch - Green */
    val BranchStreak = NeonGreen

    /** Style branch - Pink */
    val BranchStyle = NeonPink

    /** Insight branch - Purple */
    val BranchInsight = NeonPurple
}
