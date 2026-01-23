package com.example.data.mapper

import com.example.domain.model.TalentEffect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}

fun TalentEffect.toJson(): String {
    return json.encodeToString(this)
}

fun String.toTalentEffect(): TalentEffect {
    return try {
        json.decodeFromString(this)
    } catch (exception: Exception) {
        TalentEffect.None
    }
}
