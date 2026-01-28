package com.example.domain.util

import kotlin.time.Duration.Companion.milliseconds
import kotlinx.datetime.Instant as KotlinxInstant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime as kotlinxToLocalDateTime

/**
 * Convert kotlin.time.Instant to kotlinx.datetime.Instant for timezone-aware operations.
 */
fun kotlin.time.Instant.toKotlinxInstant(): KotlinxInstant {
    return KotlinxInstant.fromEpochMilliseconds(toEpochMilliseconds())
}

/**
 * Convert kotlin.time.Instant to LocalDateTime in the given timezone.
 */
fun kotlin.time.Instant.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return toKotlinxInstant().kotlinxToLocalDateTime(timeZone)
}

/**
 * Convert kotlinx.datetime.Instant to kotlin.time.Instant.
 */
fun KotlinxInstant.toKotlinTimeInstant(): kotlin.time.Instant {
    return kotlin.time.Instant.fromEpochMilliseconds(toEpochMilliseconds())
}

/**
 * Create a kotlin.time.Instant from epoch milliseconds.
 */
fun instantFromEpochMillis(epochMillis: Long): kotlin.time.Instant {
    return kotlin.time.Instant.fromEpochMilliseconds(epochMillis)
}

/**
 * Calculate duration between two kotlin.time.Instant values in milliseconds.
 */
fun kotlin.time.Instant.durationMillisUntil(other: kotlin.time.Instant): Long {
    return (other - this).inWholeMilliseconds
}
