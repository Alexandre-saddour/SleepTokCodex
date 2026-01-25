package com.example.kmpbackbone.util

import kotlinx.datetime.TimeZone

fun parseTimeZone(timeZoneId: String): TimeZone {
    return try {
        TimeZone.of(timeZoneId)
    } catch (exception: Exception) {
        TimeZone.currentSystemDefault()
    }
}
