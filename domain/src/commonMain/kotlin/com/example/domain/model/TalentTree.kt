package com.example.domain.model

data class TalentTree(
    val availablePoints: Int,
    val talents: List<Talent>,
    val unlockedTalentIds: Set<String>,
)
